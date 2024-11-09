package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.InvalidMixinException;
import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

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

    static boolean isShortCircuit(MethodInsnNode insn) {
        return insn.owner.equals("io/tunabytes/ShortCircuit") && insn.name.equals("return_");
    }

    /**
     * Edits the class node as needed.
     *
     * @param originalClassNode Class node to edit.
     * @param info Information about the mixin being transformed
     */
    void edit(ClassNode originalClassNode, MixinInfo info);

    /**
     * Applies simple changes to methods and fields instructions to make sure they have correct references
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
                if (info.isMixinEnum() && insn.desc.equals("L" + info.getMixinInternalName() + ";")) {
                    insn.desc = "L" + insn.owner + ";";
                } else {
                    MixinField mixinField = info.getMixinFields().stream()
                            .filter(mf -> mf.getType().equals(insn.desc) && mf.getNode().name.equals(insn.name))
                            .findFirst()
                            .orElseThrow(() -> new InvalidMixinException("Couldn't found remap target of an access to " + insn + " from mixin " + info.getMixinName()));
                    if (mixinField.isRemapped()) {
                        insn.desc = mixinField.getDesc();
                    }
                    if (! insn.name.equals(mixinField.getTargetFieldName())) {
                        insn.name = mixinField.getTargetFieldName();
                    }
                }
            }
        } else if (instruction instanceof MethodInsnNode) {
            MethodInsnNode insn = (MethodInsnNode) instruction;
            if (isShortCircuit(insn)) {
                return;
            }

            if (insn.getOpcode() == INVOKEINTERFACE && insn.itf && insn.owner.equals(info.getMixinInternalName())) {
                insn.setOpcode(INVOKEVIRTUAL);
                insn.itf = false;
            }

            //a call was made to a method inside the mixin class, we need to remap it
            if (insn.owner.equals(info.getMixinInternalName())) {
                insn.owner = classNode.name;
                MixinMethod mixinMethod = info.getMixinMethods().stream()
                        .filter(mm -> mm.getRealDescriptor().equals(insn.desc) &&
                                mm.getMethodNode().name.equals(insn.name))
                        .findFirst()
                        .orElseThrow(() -> new InvalidMixinException("Couldn't found remap target of a call to " + insn + " from mixin " + info.getMixinName()));
                if (mixinMethod.isRequireTypeRemapping()) {
                    insn.desc = mixinMethod.getRealDescriptor();
                }
                if (! insn.name.equals(mixinMethod.getTargetMethodName())) {
                    insn.name = mixinMethod.getTargetMethodName();
                }
            }
        } else if (instruction instanceof TypeInsnNode) {
            TypeInsnNode typeInsnNode = (TypeInsnNode) instruction;
            if (typeInsnNode.desc.equals(info.getMixinInternalName())) {
                typeInsnNode.desc = classNode.name;
            }
        }
    }

    default void handleShortCircuit(InsnList instructionList) {
        ListIterator<AbstractInsnNode> iterator = instructionList.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (!(node instanceof MethodInsnNode)) {
                continue;
            }
            MethodInsnNode insn = (MethodInsnNode) node;
            if (! isShortCircuit(insn)) {
                continue;
            }
            iterator.remove();
            switch (insn.desc) {
                case "(Ljava/lang/Object)V":
                    iterator.add(new InsnNode(Opcodes.ARETURN));
                    break;
                case "(D)V":
                    iterator.add(new InsnNode(Opcodes.DRETURN));
                    break;
                case "(F)V":
                    iterator.add(new InsnNode(Opcodes.FRETURN));
                    break;
                case "(J)V":
                    iterator.add(new InsnNode(Opcodes.LRETURN));
                    break;
                case "(C)V": //char
                case "(I)V": //int
                case "(Z)V": //bool
                case "(B)V": //byte
                    iterator.add(new InsnNode(Opcodes.IRETURN));
                    break;
            }
            boolean shouldContinue = true;
            while (shouldContinue && iterator.hasNext()) {
                AbstractInsnNode nextInst = iterator.next();
                if (nextInst instanceof LabelNode) {
                    shouldContinue = false;
                } else {
                    iterator.remove();
                }
            }
        }
    }
}
