package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A mixins editor for copying methods from the mixins class to the target class.
 */
public class MethodMergerEditor implements MixinsEditor {

    private static final Set<String> IGNORED_ENUM_METHODS = new HashSet<>(Arrays.asList("values", "valueOf", "<init>", "<clinit>"));

    @Override public void edit(ClassNode originalClassNode, MixinInfo info) {
        for (MixinMethod method : info.getMixinMethods()) {
            if (method.isAccessor()) continue;
            if (method.isInject()) continue;
            if (method.isMirror()) continue;
            if (info.isMixinEnum()) {
                if (IGNORED_ENUM_METHODS.contains(method.getTargetMethodName())) {
                    continue;
                }
            }
            //merge static initializer
            // inject no-args constructors into each constructor of the mixed class
            if (method.getTargetMethodName().equals("<init>") || method.getTargetMethodName().equals("<clinit>")) {
                merge(originalClassNode, info, method, method.getTargetMethodName());
                continue;
            }
            if (method.isOverwrite() || method.isRewrite()) continue;
            if (info.isMixinInterface() && (method.getMethodNode().access & ACC_ABSTRACT) != 0) continue;
            MethodNode mn = method.getMethodNode();
            MethodNode underlying = new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0]));
            underlying.instructions = new InsnList();
            underlying.instructions.add(mn.instructions);
            for (AbstractInsnNode instruction : underlying.instructions) {
                remapInstruction(originalClassNode, info, instruction);
            }
            originalClassNode.methods.add(underlying);
        }
    }

    private void merge(ClassNode classNode, MixinInfo info, MixinMethod method, String methodName) {
        if (! method.isOverwrite()) return;
        if (method.getDescriptor().getArgumentTypes().length == 0) {
            InsnList mixinInstructionList = method.getMethodNode().instructions;
            for (AbstractInsnNode node : mixinInstructionList) {
                if (node instanceof LineNumberNode || RETURN_OPCODES.contains(node.getOpcode())) {
                    mixinInstructionList.remove(node);
                } else if (node.getOpcode() == Opcodes.INVOKESPECIAL) {
                    MethodInsnNode nm = (MethodInsnNode) node;

                    //FIXME tester ca
                    if (nm.name.equals(methodName) && nm.owner.equals("java/lang/Object")) {//remove super() to Object ?
                        mixinInstructionList.remove(nm);
                    } else {
                        remapInstruction(classNode, info, node);
                    }
                } else {
                    remapInstruction(classNode, info, node);
                }
            }
            for (MethodNode targetMethodNode : classNode.methods) {
                if (targetMethodNode.name.equals(methodName)) {
                    AbstractInsnNode lastReturn = null;
                    for (AbstractInsnNode n : targetMethodNode.instructions) {
                        if (RETURN_OPCODES.contains(n.getOpcode())) lastReturn = n;
                    }
                    targetMethodNode.instructions.insertBefore(lastReturn, InjectionEditor.cloneInsnList(mixinInstructionList));
                }
            }
        }
    }
}
