package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.InvalidMixinException;
import io.tunabytes.bytecode.MethodNarrower;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

public class SwitchEditor implements MixinsEditor {

    @Override
    public void edit(ClassNode originalClassNode, MixinInfo info) {
        for (MixinMethod method : info.getMixinMethods()) {
            if (!method.isSwitchEditor()) continue;
            System.out.println("SwitchEditor: " + method.getMethodNode().name);
            MethodNode methodNode = MethodNarrower.tryNarrow(originalClassNode, info, method);
            for (AbstractInsnNode instruction : methodNode.instructions) {
                if (instruction instanceof TableSwitchInsnNode) {
                    TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode) instruction;
                    System.out.println("TableSwitchInsnNode: " + tableSwitchInsnNode.dflt);
                } else if (instruction instanceof LookupSwitchInsnNode) {
                    LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode) instruction;
                    System.out.println("LookupSwitchInsnNode: " + lookupSwitchInsnNode.dflt);
                }
            }
            
            throw new InvalidMixinException("No switch found in method " + method.getMethodNode().name);
        }
    }
}
