package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * A mixins editor for processing {@link io.tunabytes.Overwrite} methods.
 */
public class OverwriteEditor implements MixinsEditor {

    @SneakyThrows @Override public void edit(ClassNode classNode, MixinInfo info) {
        if (info.isMixinEnum()) return;
        for (MixinField field : info.getFields()) {
            if (field.isMirror()) continue;
            classNode.fields.add(field.getNode());
        }
        for (MixinMethod method : info.getMethods()) {
            if (method.isInject()) continue;
            if (method.isMirror()) continue;
            if (method.getName().equals("<init>")) continue;
            if (method.isOverwrite()) {
                MethodNode node = method.getMethodNode();
                if ((node.access & ACC_ABSTRACT) != 0) {
                    throw new IllegalArgumentException("@Overwrite cannot be used on abstract methods! (" + node.name + " in " + info.getMixinName() + ")");
                }
                MethodNode underlying = classNode.methods.stream().filter(c -> c.name.equals(method.getOverwrittenName()) && c.desc.equals(node.desc))
                        .findFirst().orElseThrow(() -> new NoSuchMethodException(method.getOverwrittenName()));
                underlying.instructions = new InsnList();
                underlying.instructions.add(node.instructions);
                underlying.tryCatchBlocks = node.tryCatchBlocks;
                underlying.localVariables = node.localVariables;
                underlying.maxLocals = node.maxLocals;
                underlying.exceptions = node.exceptions;
                underlying.attrs = node.attrs;
                underlying.maxStack = node.maxStack;

                for (AbstractInsnNode instruction : underlying.instructions) {
                    remapInstruction(classNode, info, instruction);
                }
            }
        }
    }
}
