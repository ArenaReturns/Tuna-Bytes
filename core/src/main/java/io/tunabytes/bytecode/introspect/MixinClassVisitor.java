package io.tunabytes.bytecode.introspect;

import io.tunabytes.Mixin;
import io.tunabytes.bytecode.InvalidMixinException;
import io.tunabytes.bytecode.introspect.MixinMethod.CallType;
import lombok.Getter;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

public class MixinClassVisitor extends ClassVisitor {
    private static final Type MIXIN = Type.getType(Mixin.class);

    private final List<MixinField> fields = new ArrayList<>();
    private final List<MixinMethod> methods = new ArrayList<>();
    private final Set<String> interfaceToAdd = new HashSet<>();
    private boolean isInterface;
    private boolean isEnum;
    private boolean addAllInterfacesToMixins = true;
    @Getter
    private boolean hasMirroredParent;
    protected Set<String> deletedEnumValues = new HashSet<>();
    @Getter
    private String name;
    @Getter
    private MixinInfo info;

    public MixinClassVisitor() {
        super(Opcodes.ASM8);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if ((access & Opcodes.ACC_INTERFACE) != 0)
            isInterface = true;
        this.name = name.replace('/', '.');
        Collections.addAll(this.interfaceToAdd, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (MIXIN.getDescriptor().equals(descriptor)) {
            return new AnnotationVisitor(Opcodes.ASM8) {
                @Override
                public void visit(String name, Object value) {
                    super.visit(name, value);
                    if ("addAllInterfacesToMixins".equals(name)) {
                        addAllInterfacesToMixins = (boolean) value;
                    } else if ("withFakeParentAccessor".equals(name)) {
                        hasMirroredParent = (boolean) value;
                    } else if ("enumTarget".equals(name)) {
                        isEnum = (boolean) value;
                    } else if ("deletedEnumConstants".equals(name)) {
                        String[] deletedEnumConstants = (String[]) value;
                        for (String deletedEnumConstant : deletedEnumConstants) {
                            String trimmedValue = deletedEnumConstant.trim();
                            if (!trimmedValue.isEmpty()) {
                                deletedEnumValues.add(trimmedValue);
                            }
                        }
                    }
                }
            };
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String fname, String descriptor, String signature, Object value) {
        return new MixinFieldVisitor(this, new FieldNode(access, fname, descriptor, signature, value)) {
            @Override
            public void visitEnd() {
                FieldNode node = (FieldNode) fv;
                node.desc = desc;
                if ((access & Opcodes.ACC_FINAL) != 0 && mirror) {
                    throw new InvalidMixinException("Field '" + descriptor + " " + fname + "' in class '" + MixinClassVisitor.this.name + "' must not be final if it has @Mirror !");
                }
                fields.add(new MixinField(access, mirror, definalize, name == null ? fname : name, desc, remapped, enumField, descriptor, (FieldNode) fv));
            }
        };
    }

    @Override
    public MethodVisitor visitMethod(int access, String visitedMethodName, String descriptor, String signature, String[] exceptions) {
         return new MixinMethodVisitor(this, new MethodNode(access, visitedMethodName, descriptor, signature, exceptions)) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                Type desc = Type.getMethodType(returnType, argumentTypes);
                mixinMethodNode.desc = desc.getDescriptor();
                methods.add(new MixinMethod(
                        targetMethodName,
                        access,
                        desc,
                        descriptor,
                        injectLine,
                        injectLineReplaceEnd,
                        manualInstructionSkip,
                        lastParameterArgIndex,
                        injectAt,
                        overwrite,
                        rewrite,
                        accessor,
                        inject,
                        mirror,
                        definalize,
                        requireTypeRemapping,
                        keepLastReturn,
                        runtimeRewriter,
                        accessorName == null ? getActualName(visitedMethodName) : accessorName,
                        mixinMethodNode,
                        type == null ? CallType.INVOKE : type,
                        localCapture, privateClassAccessor, ldcSwapInfoList
                ));
            }
        };
    }

    @Override
    public void visitEnd() {
        if (! addAllInterfacesToMixins) {
            interfaceToAdd.clear();
        }
        info = new MixinInfo(name, name.replace('.', '/'),
                isInterface, isEnum, hasMirroredParent, fields, methods, deletedEnumValues, interfaceToAdd);
        super.visitEnd();
    }
}
