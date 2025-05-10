package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class PrivateClassAccessorEditor implements io.tunabytes.bytecode.editor.MixinsEditor {
    @Override
    public void edit(ClassNode originalClassNode, MixinInfo info) {
        for (MixinMethod mixinMethod : info.getMixinMethods()) {
            if (mixinMethod.isPrivateClassAccessor()) {
                MethodNode methodNode = mixinMethod.getMethodNode();

                methodNode.instructions = new InsnList();
                methodNode.instructions.add(new LdcInsnNode(Type.getType("L" + mixinMethod.getAccessedProperty().replace('.', '/') + ";")));
                methodNode.instructions.add(new InsnNode(ARETURN));
            }
        }
    }
}
