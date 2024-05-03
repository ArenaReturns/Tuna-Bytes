package io.tunabytes.bytecode.introspect;

import io.tunabytes.Accessor;
import io.tunabytes.ActualType;
import io.tunabytes.Definalize;
import io.tunabytes.Inject;
import io.tunabytes.Inject.At;
import io.tunabytes.Mirror;
import io.tunabytes.Overwrite;
import io.tunabytes.Rewrite;
import io.tunabytes.Rewrite.Rewritter;
import io.tunabytes.bytecode.InvalidMixinException;
import io.tunabytes.bytecode.introspect.MixinMethod.CallType;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class MixinMethodVisitor extends MethodVisitor {

    public static final String STACK_SEPARATOR_DESC = "Lio/tunabytes/StackSeparator;";
    private static final String OVERWRITE = Type.getDescriptor(Overwrite.class);
    private static final String INJECT = Type.getDescriptor(Inject.class);
    private static final String ACCESSOR = Type.getDescriptor(Accessor.class);
    private static final String REWRITE = Type.getDescriptor(Rewrite.class);
    private static final String MIRROR = Type.getDescriptor(Mirror.class);
    private static final String DEFINALIZE = Type.getDescriptor(Definalize.class);
    private static final String ACTUAL_TYPE = Type.getDescriptor(ActualType.class);
    protected MethodNode node;
    protected String mirrorName;
    protected Type returnType;
    protected Type[] argumentTypes;
    protected boolean overwrite, inject, accessor, mirror, definalize, remap, keepLastReturn, rewrite;
    protected String overwrittenName;
    protected String injectMethodName;
    protected Class<? extends Rewritter> runtimeRewriter;
    protected String accessorName;
    protected int injectLine;
    protected int injectLineReplaceEnd = -1;
    protected int lastParameterArgIndex = Integer.MAX_VALUE;
    protected At injectAt;
    protected CallType type = CallType.INVOKE;

    public MixinMethodVisitor(MethodNode node) {
        super(Opcodes.ASM8, node);
        this.node = node;
        Type desc = Type.getMethodType(node.desc);
        returnType = desc.getReturnType();
        argumentTypes = desc.getArgumentTypes();
    }

    private static String normalize(String prefix, String value) {
        if (value.length() > prefix.length()) {
            return Character.toLowerCase(value.charAt(prefix.length())) + value.substring(prefix.length() + 1);
        }
        return value;
    }

    public static Type fromActualType(String descriptor, String actualType) {
        String arrayAddition = "";
        if (descriptor.startsWith("[")) {
            arrayAddition = descriptor.substring(0, descriptor.lastIndexOf('[') + 1);
        }
        return Type.getType(arrayAddition + "L" + actualType.replace('.', '/') + ";");
    }

    public static Class<?> getClassForInternalName(String classDesc) {
        String className = classDesc.replace('/', '.');
        try {
            return MixinMethodVisitor.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            // If class not found trying the context classLoader
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException e2) {
                throw new RuntimeException("Error loading class '" + className + "' for rule method analysis", e2);
            }
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        boolean visitingRewrite = REWRITE.equals(descriptor);
        boolean visitingAccessor = ACCESSOR.equals(descriptor);
        boolean visitingOverwrite = OVERWRITE.equals(descriptor);
        boolean visitingInject = INJECT.equals(descriptor);
        boolean visitingMirror = MIRROR.equals(descriptor);
        boolean visitingActualType = ACTUAL_TYPE.equals(descriptor);
        if (visitingRewrite) {
            rewrite = true;
        }
        if (visitingOverwrite) {
            overwrite = true;
        }
        if (visitingInject) {
            inject = true;
            injectMethodName = node.name;
        }
        if (visitingAccessor) {
            accessor = true;
        }
        if (visitingMirror) {
            mirror = true;
        }
        if (DEFINALIZE.equals(descriptor)) {
            definalize = true;
        }
        return new AnnotationVisitor(Opcodes.ASM8, super.visitAnnotation(descriptor, visible)) {
            @Override
            public void visit(String name, Object value) {
                super.visit(name, value);
                if (visitingAccessor && name.equals("value")) {
                    accessorName = (String) value;
                }
                if ((visitingRewrite || visitingOverwrite) && name.equals("value")) {
                    overwrittenName = (String) value;
                }
                if (visitingRewrite && name.equals("runtimeRewriter")) {
                    runtimeRewriter = (Class<? extends Rewritter>) getClassForInternalName(((Type) value).getInternalName());
                }
                if (visitingMirror && name.equals("value")) {
                    mirrorName = (String) value;
                }
                if (visitingActualType && name.equals("value")) {
                    remap = true;
                    String rtype = returnType.getDescriptor();
                    returnType = fromActualType(rtype, (String) value);
                }
                if (visitingInject) {
                    switch (name) {
                        case "method": {
                            injectMethodName = (String) value;
                            break;
                        }
                        case "returnType": {
                            returnType = Type.getType((String) value);
                            break;
                        }
                        case "lineNumber": {
                            injectLine = (int) value;
                            break;
                        }
                        case "keepLastReturn": {
                            keepLastReturn = (boolean) value;
                            break;
                        }
                        case "injectLineReplaceEnd": {
                            injectLineReplaceEnd = (int) value;
                            break;
                        }
                    }
                }
            }

            @Override
            public void visitEnum(String name, String descriptor, String value) {
                super.visitEnum(name, descriptor, value);
                if (visitingInject && name.equals("at")) {
                    injectAt = At.at(value);
                }
            }
        };
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
//        Important note: <i>a parameter index i
//   *     is not required to correspond to the i'th parameter descriptor in the method
//   *     descriptor</i>, in particular in case of synthetic parameters (see
//   *     https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
        //FIXME
        if (STACK_SEPARATOR_DESC.equals(descriptor)) {
            if (lastParameterArgIndex == Integer.MAX_VALUE) {
                lastParameterArgIndex = parameter;
            } else {
                throw new InvalidMixinException("Double @StackSeparator annotation for method " + injectMethodName);
            }
        }
        return new AnnotationVisitor(Opcodes.ASM8, super.visitParameterAnnotation(parameter, descriptor, visible)) {
            @Override
            public void visit(String name, Object value) {
                remap = true;
                if (ACTUAL_TYPE.equals(descriptor)) {
                    argumentTypes[parameter] = fromActualType(argumentTypes[parameter].getDescriptor(), (String) value);
                }
                super.visit(name, value);
            }
        };
    }

    protected String getActualName(String accessorName) {
        if (accessorName.startsWith("get")) {
            type = CallType.GET;
            return normalize("get", accessorName);
        }
        if (accessorName.startsWith("set")) {
            type = CallType.SET;
            return normalize("set", accessorName);
        }
        if (accessorName.startsWith("is")) {
            type = CallType.GET;
            return normalize("is", accessorName);
        }
        if (accessorName.startsWith("call")) {
            type = CallType.INVOKE;
            return normalize("call", accessorName);
        }
        if (accessorName.startsWith("invoke")) {
            type = CallType.INVOKE;
            return normalize("invoke", accessorName);
        }
        return accessorName;
    }


}
