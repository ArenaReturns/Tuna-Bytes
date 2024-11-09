package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * A mixins editor for processing {@link io.tunabytes.Definalize} fields.
 */
public class DefinalizeEditor implements MixinsEditor {

    @SneakyThrows @Override public void edit(ClassNode originalClassNode, MixinInfo info) {
        for (MixinField field : info.getMixinFields()) {
            if (field.isDefinalize() && field.isMirror()) {
                FieldNode fnode = originalClassNode.fields.stream().filter(c -> c.name.equals(field.getTargetFieldName())).findFirst()
                        .orElseThrow(() -> new NoSuchFieldException(field.getTargetFieldName()));
                fnode.access &= ~Opcodes.ACC_FINAL;
            }
        }
        for (MixinMethod method : info.getMixinMethods()) {
            if (method.isDefinalize() && method.isMirror()) {
                MethodNode mnode = originalClassNode.methods.stream().filter(c -> c.name.equals(method.getTargetMethodName())).findFirst()
                        .orElseThrow(() -> new NoSuchFieldException(method.getTargetMethodName()));
                mnode.access &= ~Opcodes.ACC_FINAL;
            }
        }
    }
}
