package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a transformer for editing class nodes.
 */
public interface MixinsEditor extends Opcodes {

    List<Integer> RETURN_OPCODES = Arrays.asList(
            RETURN,
            ARETURN,
            IRETURN,
            DRETURN,
            FRETURN,
            LRETURN
    );

    /**
     * Edits the class node as needed.
     *
     * @param node Class node to edit.
     * @param info Information about the mixin being transformed
     */
    void edit(ClassNode node, MixinInfo info);

    /**
     * Applies simple changes to methods and fields instructions to make sure they
     * have correct references
     *
     * @param classNode   Class node to remap to
     * @param info        Mixins information
     * @param instruction The instruction to remap
     */
    default void remapInstruction(ClassNode classNode, MixinInfo info, AbstractInsnNode instruction) {
        if (instruction instanceof FieldInsnNode) {
            FieldInsnNode insn = (FieldInsnNode) instruction;
            if (insn.owner.equals(info.getMixinInternalName())) {
                insn.owner = classNode.name;
                String mixinNameArray = "L" + info.getMixinInternalName() + ";";
                if (info.isMixinEnum() && insn.desc.equals(mixinNameArray)) {
                    insn.desc = "L" + insn.owner + ";";
                } else {
                    info.getFields().stream()
                            .filter(MixinField::isRemapped)
                            .filter(c -> c.getType().equals(insn.desc))
                            .findFirst()
                            .ifPresent(field -> insn.desc = field.getDesc());
                }
            }
        }
        if (instruction instanceof MethodInsnNode) {
            MethodInsnNode insn = (MethodInsnNode) instruction;
            if (insn.getOpcode() == INVOKEINTERFACE && insn.itf && insn.owner.equals(info.getMixinInternalName())) {
                insn.setOpcode(INVOKEVIRTUAL);
                insn.itf = false;
            }
            if (info.isMirrorParent() && insn.getOpcode() == INVOKESPECIAL) {
                insn.owner = classNode.superName;
            }
            if (insn.owner.equals(info.getMixinInternalName())) {
                insn.owner = classNode.name;
                info.getMethods().stream()
                        .filter(MixinMethod::isRequireTypeRemapping)
                        .filter(c -> c.getRealDescriptor().equals(insn.desc))
                        .findFirst()
                        .ifPresent(method -> insn.desc = method.getDescriptor().getDescriptor());
            }
        }
        if (instruction instanceof TypeInsnNode) {
            TypeInsnNode typeInsnNode = (TypeInsnNode) instruction;
            if (typeInsnNode.desc.equals(info.getMixinInternalName())) {
                typeInsnNode.desc = classNode.name;
            }
        }
    }
}
