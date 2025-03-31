package io.tunabytes.bytecode.introspect;

import io.tunabytes.*;
import io.tunabytes.Inject.At;
import io.tunabytes.Rewrite.Rewritter;
import io.tunabytes.bytecode.InvalidMixinException;
import io.tunabytes.bytecode.introspect.MixinMethod.CallType;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;

public class MixinMethodVisitor extends MethodVisitor {

    public static final String STACK_SEPARATOR_DESC = "Lio/tunabytes/StackSeparator;";
    private static final String OVERWRITE = Type.getDescriptor(Overwrite.class);
    private static final String INJECT = Type.getDescriptor(Inject.class);
    private static final String ACCESSOR = Type.getDescriptor(Accessor.class);
    private static final String REWRITE = Type.getDescriptor(Rewrite.class);
    private static final String MIRROR = Type.getDescriptor(Mirror.class);
    private static final String DEFINALIZE = Type.getDescriptor(Definalize.class);
    private static final String ACTUAL_TYPE = Type.getDescriptor(ActualType.class);

    private final MixinClassVisitor mixinClassVisitor;
    protected MethodNode mixinMethodNode;
    protected Type returnType;
    protected Type[] argumentTypes;
    protected boolean overwrite, inject, accessor, mirror, definalize, requireTypeRemapping, keepLastReturn, rewrite, forceLowerCase = true;
    protected String targetMethodName;
    protected Class<? extends Rewritter> runtimeRewriter;
    protected String accessorName;
    protected int injectLine = 0;
    protected int injectLineReplaceEnd = Integer.MIN_VALUE;
    protected int lastParameterArgIndex = Integer.MAX_VALUE;
    protected At injectAt;
    protected CallType type;
    protected int manualInstructionSkip = 0;
    protected boolean localCapture = false;

    public MixinMethodVisitor(MixinClassVisitor mixinClassVisitor, MethodNode node) {
        super(Opcodes.ASM8, node);
        this.mixinClassVisitor = mixinClassVisitor;
        this.mixinMethodNode = node;
        Type desc = Type.getMethodType(node.desc);
        returnType = desc.getReturnType();
        argumentTypes = desc.getArgumentTypes();
    }

    private static String normalize(String prefix, String value, boolean forceLowercase) {
        if (value.length() > prefix.length()) {
            char firstLetter = value.charAt(prefix.length());
            if (forceLowercase) {
                firstLetter = Character.toLowerCase(firstLetter);
            }
            return firstLetter + value.substring(prefix.length() + 1);
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
        super.visitAnnotation(descriptor, visible);
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
                if (visitingAccessor) {
                    switch (name) {
                        case "value":
                            accessorName = (String) value;
                            break;
                        case "nameStartWithLowercase":
                            forceLowerCase = (boolean) value;
                            break;
                    }
                }
                if ((visitingRewrite || visitingOverwrite || visitingMirror) && name.equals("value")) {
                    targetMethodName = (String) value;
                }
                if (visitingRewrite && name.equals("runtimeRewriter")) {
                    runtimeRewriter = (Class<? extends Rewritter>) getClassForInternalName(((Type) value).getInternalName());
                }
                if (visitingActualType && name.equals("value")) {
                    requireTypeRemapping = true;
                    String rtype = returnType.getDescriptor();
                    returnType = fromActualType(rtype, (String) value);
                }
                if (visitingInject) {
                    switch (name) {
                        case "method": {
                            targetMethodName = (String) value;
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
                        case "manualInstructionSkip": {
                            manualInstructionSkip = (int) value;
                            break;
                        }
                        case "localCapture": {
                            localCapture = (boolean) value;
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
                } else if (visitingAccessor && name.equals("type")) {
                    type = CallType.valueOf(value);
                }
            }

            @Override
            public void visitEnd() {
                super.visitEnd();
                if (inject) {
                    if (injectAt == At.BEFORE_LINE || injectAt == At.AFTER_LINE) {
                        if (injectLineReplaceEnd != Integer.MIN_VALUE) {
                            throw new InvalidMixinException("@Inject injectLineReplaceEnd must not be specified with injectAt mode " + injectAt + getErrorLocation());
                        }
                        if (injectLine < 1) {
                            throw new InvalidMixinException("@Inject injectLine must be > 0" + getErrorLocation());
                        }
                    } else if (injectAt == At.REPLACE_LINE) {
                        if (injectLine < 1) {
                            throw new InvalidMixinException("@Inject injectLine must be > 0" + getErrorLocation());
                        }
                        //if not specified, we only replace the line at 'injectLine'
                        if (injectLineReplaceEnd == Integer.MIN_VALUE) {
                            injectLineReplaceEnd = injectLine;
                        } else {
                            if (injectLineReplaceEnd < injectLine) {
                                throw new InvalidMixinException("@Inject injectLineReplaceEnd must be specified and >= injectLine (" + injectLine + ") " + getErrorLocation());
                            }
                        }
                    }
                }
                if (overwrite && (mixinMethodNode.access & ACC_ABSTRACT) != 0) {
                    throw new InvalidMixinException("@Overwrite cannot be used on abstract mixin methods!" + getErrorLocation());
                }
                if (rewrite && (mixinMethodNode.access & ACC_ABSTRACT) == 0) {
                    throw new InvalidMixinException("@Rewrite must be used on abstract mixin methods! at " + getErrorLocation());
                }
            }
        };
    }

    private String getErrorLocation() {
       return  " at " + mixinClassVisitor.getName() + '.' + mixinMethodNode.name + mixinMethodNode.desc;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        super.visitParameterAnnotation(parameter, descriptor, visible);
//        Important note: <i>a parameter index i
//   *     is not required to correspond to the i'th parameter descriptor in the method
//   *     descriptor</i>, in particular in case of synthetic parameters (see
//   *     https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
        //FIXME
        if (STACK_SEPARATOR_DESC.equals(descriptor)) {
            if (lastParameterArgIndex == Integer.MAX_VALUE) {
                lastParameterArgIndex = parameter;
            } else {
                throw new InvalidMixinException("Double @StackSeparator annotation for method " + targetMethodName);
            }
        }
        return new AnnotationVisitor(Opcodes.ASM8, super.visitParameterAnnotation(parameter, descriptor, visible)) {
            @Override
            public void visit(String name, Object value) {
                requireTypeRemapping = true;
                if (ACTUAL_TYPE.equals(descriptor)) {
                    argumentTypes[parameter] = fromActualType(argumentTypes[parameter].getDescriptor(), (String) value);
                }
                super.visit(name, value);
            }
        };
    }

    protected String getActualName(String accessorName) {
        String prefix;
        if (accessorName.startsWith("get")) {
            prefix = "get";
            if (type == null) {
                type = CallType.GET;
            }
        } else if (accessorName.startsWith("set")) {
            prefix = "set";
            if (type == null) {
                type = CallType.SET;
            }
        } else if (accessorName.startsWith("is")) {
            prefix = "is";
            if (type == null) {
                type = CallType.GET;
            }
        } else if (accessorName.startsWith("call")) {
            prefix = "call";
            if (type == null) {
                type = CallType.INVOKE;
            }
        } else if (accessorName.startsWith("invoke")) {
            prefix = "invoke";
            if (type == null) {
                type = CallType.INVOKE;
            }
        } else {
            if (type == null) {
                type = CallType.INVOKE;
            }
            return accessorName;
        }
        return normalize(prefix, accessorName, forceLowerCase);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if (targetMethodName == null) {
            targetMethodName = mixinMethodNode.name;
        }
    }
}
