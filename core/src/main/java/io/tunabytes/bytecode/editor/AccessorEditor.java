package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import io.tunabytes.bytecode.introspect.MixinMethod.CallType;
import lombok.SneakyThrows;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

/**
 * A mixins editor for processing {@link io.tunabytes.Accessor} methods.
 */
public class AccessorEditor implements MixinsEditor {

    @SneakyThrows @Override public void edit(ClassNode originalClassNode, MixinInfo info) {
        if (info.isMixinInterface()) {
            originalClassNode.interfaces.add(info.getMixinInternalName());
            for (MixinMethod method : info.getMixinMethods()) {
                if (!method.isAccessor()) continue;
                if ((method.getMethodNode().access & ACC_ABSTRACT) == 0) {
                    throw new IllegalArgumentException("@Accessor cannot be used on non-abstract methods! (" + originalClassNode.name + " in " + info.getMixinName() + ")");
                }
                MethodNode n = method.getMethodNode();
                MethodNode impl = new MethodNode(ACC_PUBLIC, method.getTargetMethodName(), method.getDescriptor().getDescriptor(), n.signature, n.exceptions.toArray(new String[0]));
                Type targetType = Type.getMethodType(n.desc);
                Type returnType = targetType.getReturnType();
                Type[] arguments = targetType.getArgumentTypes();
                if (method.getType() == CallType.GET) {
                    targetType = targetType.getReturnType();
                } else if (method.getType() == CallType.SET) {
                    if (arguments.length != 1) {
                        throw new IllegalArgumentException("Accessor setter must have exactly one argument! (" + originalClassNode.name + " in " + info.getMixinName() + ")");
                    }
                    targetType = arguments[0];
                }

                boolean isStatic;
                switch (method.getType()) {
                    case GET:
                        FieldNode accessed = originalClassNode.fields.stream().filter(c -> c.name.equals(method.getAccessedProperty())).findFirst()
                                .orElseThrow(() -> new NoSuchFieldException(method.getAccessedProperty()));
                        isStatic = (accessed.access & ACC_STATIC) != 0;
                        if (!isStatic) {
                            impl.instructions.add(new VarInsnNode(ALOAD, 0));
                        }
                        impl.instructions.add(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, originalClassNode.name, method.getAccessedProperty(), targetType.getDescriptor()));
                        impl.instructions.add(new InsnNode(targetType.getOpcode(IRETURN)));
                        break;
                    case SET:
                        accessed = originalClassNode.fields.stream().filter(c -> c.name.equals(method.getAccessedProperty())).findFirst()
                                .orElseThrow(() -> new NoSuchFieldException(method.getAccessedProperty()));
                        isStatic = (accessed.access & ACC_STATIC) != 0;
                        if (!isStatic)
                            impl.instructions.add(new VarInsnNode(ALOAD, 0));
                        if ((accessed.access & ACC_FINAL) != 0)
                            accessed.access &= ~ACC_FINAL;
                        impl.instructions.add(new VarInsnNode(targetType.getOpcode(ILOAD), getArgIndex(0, isStatic, arguments)));
                        impl.instructions.add(new FieldInsnNode(PUTFIELD, originalClassNode.name, method.getAccessedProperty(), targetType.getDescriptor()));
                        impl.instructions.add(new InsnNode(returnType.getOpcode(IRETURN)));
                        break;
                    case INVOKE:
                        MethodNode accessedMethod = originalClassNode.methods.stream().filter(m -> m.name.equals(method.getAccessedProperty()) && m.desc.equals(n.desc))
                                .findFirst().orElseThrow(() -> new NoSuchMethodException(method.getAccessedProperty()));
                        isStatic = (accessedMethod.access & ACC_STATIC) != 0;
                        if (!isStatic)
                            impl.instructions.add(new VarInsnNode(ALOAD, 0));
                        for (int i = 0; i < targetType.getArgumentTypes().length; i++) {
                            impl.instructions.add(new VarInsnNode(arguments[i].getOpcode(ILOAD), getArgIndex(i, isStatic, arguments)));
                        }
                        if (isStatic)
                            impl.instructions.add(new MethodInsnNode(INVOKESTATIC, originalClassNode.name, method.getAccessedProperty(), method.getDescriptor().getDescriptor()));
                        else {
                            if (method.isPrivate() || accessedMethod.name.endsWith("init>"))
                                impl.instructions.add(new MethodInsnNode(INVOKESPECIAL, originalClassNode.name, method.getAccessedProperty(), method.getDescriptor().getDescriptor()));
                            else
                                impl.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, originalClassNode.name, method.getAccessedProperty(), method.getDescriptor().getDescriptor()));
                        }
                        impl.instructions.add(new InsnNode(targetType.getReturnType().getOpcode(IRETURN)));
                        break;
                }
                originalClassNode.methods.add(impl);
            }
        } else {
            originalClassNode.interfaces.addAll(info.getInterfacesToAdd());
        }
    }

    private int getArgIndex(final int arg, boolean isStatic, Type[] args) {
        int index = isStatic ? 0 : 1;
        for (int i = 0; i < arg; i++) {
            index += args[i].getSize();
        }
        return index;
    }
}
