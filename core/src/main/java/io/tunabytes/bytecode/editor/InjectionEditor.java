package io.tunabytes.bytecode.editor;

import io.tunabytes.Inject.At;
import io.tunabytes.bytecode.ClassNarrower;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.*;

import java.util.*;

/**
 * A mixins editor for processing {@link io.tunabytes.Inject} methods.
 */
public class InjectionEditor implements MixinsEditor {

    @SneakyThrows @Override 
    public void edit(ClassNode originalClassNode, MixinInfo info) {
        for (MixinMethod method : info.getMixinMethods()) {
            if (!method.isInject()) continue;
            if ((method.getMethodNode().access & ACC_ABSTRACT) != 0) {
                throw new IllegalArgumentException("@Inject cannot be used on abstract methods! (" + method.getMethodNode().name + " in " + info.getMixinName() + ")");
            }
            At at = method.getInjectAt();
            int injectLine = method.getInjectLine();
            MethodNode targetMethod = ClassNarrower.tryNarrow(originalClassNode, info, method);

            InsnList list = method.getMethodNode().instructions;
            for (AbstractInsnNode instruction : list) {
                remapInstruction(originalClassNode, info, instruction);
            }

            AbstractInsnNode lastInjectedReturn = null;
            for (AbstractInsnNode abstractInsnNode : list) {
                if (abstractInsnNode instanceof LineNumberNode) {
                    list.remove(abstractInsnNode);
                } else if (RETURN_OPCODES.contains(abstractInsnNode.getOpcode())) {
                    lastInjectedReturn = abstractInsnNode;
                }
            }

            if (! method.isKeepLastReturn()) {
                if (lastInjectedReturn != null) list.remove(lastInjectedReturn);
            }
            
            handleShortCircuit(list);

            if (at == At.BEGINNING) {
                AbstractInsnNode first = targetMethod.instructions.getFirst();
                if (first != null) {
                    int manualBacktrack = 0;
                    while (manualBacktrack < method.getManualInstructionSkip()) {
                        first = first.getNext();
                    }
                    targetMethod.instructions.insert(first, list);
                } else {
                    targetMethod.instructions.add(list);
                }
            } else if (at == At.END) {
                AbstractInsnNode lastReturn = null;
                for (AbstractInsnNode instruction : targetMethod.instructions) {
                    if (instruction instanceof InsnNode && RETURN_OPCODES.contains(instruction.getOpcode()))
                        lastReturn = instruction;
                }
                targetMethod.instructions.insertBefore(lastReturn, list);
            } else if (at == At.BEFORE_EACH_RETURN) {
                for (AbstractInsnNode insnNode : targetMethod.instructions) {
                    if (RETURN_OPCODES.contains(insnNode.getOpcode())) {
                        targetMethod.instructions.insertBefore(insnNode, cloneInsnList(list));
                    }
                }
            } else if (at == At.BEFORE_LINE) {
                for (AbstractInsnNode insnNode : targetMethod.instructions) {
                    if (!(insnNode instanceof LineNumberNode)) continue;
                    int currentLine = ((LineNumberNode) insnNode).line;
                    if (currentLine == injectLine) {
                        if (insnNode.getPrevious() instanceof LabelNode) {
                            insnNode = insnNode.getPrevious();
                        }
                        int manualBacktrack = 0;
                        while (manualBacktrack < method.getManualInstructionSkip()) {
                            insnNode = insnNode.getPrevious();
                            manualBacktrack++;
                        }
                        targetMethod.instructions.insertBefore(insnNode, list);
                        break;
                    }
                }
            } else if (at == At.AFTER_LINE) {
                for (AbstractInsnNode insnNode : targetMethod.instructions) {
                    if (!(insnNode instanceof LineNumberNode)) continue;
                    int currentLine = ((LineNumberNode) insnNode).line;
                    if (currentLine == injectLine) {
                        int manualBacktrack = 0;
                        while (manualBacktrack < method.getManualInstructionSkip()) {
                            insnNode = insnNode.getNext();
                            manualBacktrack++;
                        }
                        targetMethod.instructions.insert(insnNode, list);
                        break;
                    }
                }
            } else if (at == At.REPLACE_LINE) {
                AbstractInsnNode first = null;
                int injectLineReplaceEnd = method.getInjectLineReplaceEnd();

                List<AbstractInsnNode> toRemoveNodes = new ArrayList<>();
                for (ListIterator<AbstractInsnNode> iterator = targetMethod.instructions.iterator(); iterator.hasNext();) {
                    AbstractInsnNode insnNode = iterator.next();
                    if (!(insnNode instanceof LineNumberNode)) {
                        continue;
                    }
                    first = insnNode;

                    int currentLine = ((LineNumberNode) insnNode).line;
                    if (currentLine == injectLine) {
                        while (iterator.hasNext()) {
                            insnNode = iterator.next();
                            if (!(insnNode instanceof LineNumberNode)) {
                                toRemoveNodes.add(insnNode);
                                continue;
                            }
                            currentLine = ((LineNumberNode) insnNode).line;
                            if (currentLine > injectLineReplaceEnd) {
                                break;
                            } else {
                                toRemoveNodes.add(insnNode);
                            }
                        }
                        break;
                    }
                }

                if (toRemoveNodes.get(toRemoveNodes.size() - 1) instanceof LabelNode) {
                    toRemoveNodes.remove(toRemoveNodes.size() - 1);
                }

                toRemoveNodes.forEach(a -> targetMethod.instructions.remove(a));

                if (first == null) {
                    targetMethod.instructions.add(list);
                } else {
                    targetMethod.instructions.insert(first, list);
                }

            }
        }
    }

    private static Map<LabelNode, LabelNode> cloneLabels(InsnList insns) {
        Map<LabelNode, LabelNode> labelMap = new HashMap<>();
        for (AbstractInsnNode insn = insns.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getType() == 8) {
                labelMap.put((LabelNode) insn, new LabelNode());
            }
        }
        return labelMap;
    }

    public static InsnList cloneInsnList(InsnList insns) {
        return cloneInsnList(cloneLabels(insns), insns);
    }

    private static InsnList cloneInsnList(Map<LabelNode, LabelNode> labelMap, InsnList insns) {
        InsnList clone = new InsnList();
        for (AbstractInsnNode insn = insns.getFirst(); insn != null; insn = insn.getNext()) {
            clone.add(insn.clone(labelMap));
        }
        return clone;
    }
}
