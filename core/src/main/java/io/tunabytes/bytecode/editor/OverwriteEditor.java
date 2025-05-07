package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.MethodNarrower;
import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * A mixins editor for processing {@link io.tunabytes.Overwrite} methods.
 */
public class OverwriteEditor implements MixinsEditor {

    @SneakyThrows @Override public void edit(ClassNode clazzToMixin, MixinInfo info) {
        if (info.isMixinEnum()) return;
        for (MixinField field : info.getMixinFields()) {
            if (field.isMirror()) continue;
            clazzToMixin.fields.add(field.getNode());
        }
        for (MixinMethod mixinMethod : info.getMixinMethods()) {
            if (mixinMethod.isInject()) continue;
            if (mixinMethod.isMirror()) continue;
            if (mixinMethod.getTargetMethodName().equals("<init>")) continue;
            if (mixinMethod.isOverwrite()) {
                MethodNode mixinMethodNode = mixinMethod.getMethodNode();
                MethodNode methodToMixin = MethodNarrower.tryNarrow(clazzToMixin, info, mixinMethod);

                methodToMixin.instructions = new InsnList();
                methodToMixin.instructions.add(mixinMethodNode.instructions);
                methodToMixin.tryCatchBlocks = mixinMethodNode.tryCatchBlocks;
                methodToMixin.localVariables = mixinMethodNode.localVariables;
                methodToMixin.maxLocals = mixinMethodNode.maxLocals;
                methodToMixin.exceptions = mixinMethodNode.exceptions;
                methodToMixin.attrs = mixinMethodNode.attrs;
                methodToMixin.maxStack = mixinMethodNode.maxStack;
                methodToMixin.access = mixinMethodNode.access;

                for (AbstractInsnNode instruction : methodToMixin.instructions) {
                    remapInstruction(clazzToMixin, info, instruction);
                }
            }
        }
    }
}
