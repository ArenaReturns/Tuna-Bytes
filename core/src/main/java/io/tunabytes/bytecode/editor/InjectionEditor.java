package io.tunabytes.bytecode.editor;

import io.tunabytes.Inject.At;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A mixins editor for processing {@link io.tunabytes.Inject} methods.
 */
public class InjectionEditor implements MixinsEditor {

    @SneakyThrows @Override public void edit(ClassNode classNode, MixinInfo info) {
        for (MixinMethod method : info.getMethods()) {
            if (!method.isInject()) continue;
            if ((method.getMethodNode().access & ACC_ABSTRACT) != 0) {
                throw new IllegalArgumentException("@Inject cannot be used on abstract methods! (" + method.getMethodNode().name + " in " + info.getMixinName() + ")");
            }
            At at = method.getInjectAt();
            int line = method.getInjectLine();
            String injectIn = method.getInjectMethod();
            List<MethodNode> collected = classNode.methods.stream()
                    .filter(c -> c.name.equals(injectIn))
                    .collect(Collectors.toList());
            if (collected.size() > 1) {
                String error = " target(s) found for patch " + info.getMixinName() + "#" + method.getName();
                System.out.println("[warn]" + collected.size() + error + " narrowing down with descriptor...");
                Type[] arguments = Type.getArgumentTypes(method.getRealDescriptor());
                Type returnType = Type.getReturnType(method.getRealDescriptor());
                Type[] substringArgument = Arrays.copyOf(arguments, method.getLastParameterArgIndex());
                collected = collected.stream()
                    .filter(m -> {
                        String desc = m.desc;
                        return Type.getReturnType(desc) == returnType &&
                                Arrays.equals(Type.getArgumentTypes(desc), substringArgument);
                    }).collect(Collectors.toList());
                if (collected.size() > 1) {
                    throw new NoSuchMethodException(error + "\n" + collected.stream().map(methodNode -> "- " + methodNode.desc).collect(Collectors.joining("\n")));
                }
            }

            MethodNode targetMethod = collected.get(0);

            InsnList list = method.getMethodNode().instructions;
            for (AbstractInsnNode instruction : list) {
                remapInstruction(classNode, info, instruction);
            }

            AbstractInsnNode lastInjectedReturn = null;
            for (AbstractInsnNode abstractInsnNode : list) {
                if (abstractInsnNode instanceof LineNumberNode) {
                    list.remove(abstractInsnNode);
                } else if (RETURN_OPCODES.contains(abstractInsnNode.getOpcode())) {
                    lastInjectedReturn = abstractInsnNode;
                }
            }

            if (lastInjectedReturn != null) list.remove(lastInjectedReturn);

            if (at == At.BEGINNING) {
                AbstractInsnNode first = targetMethod.instructions.getFirst();
                if (first != null) {
                    targetMethod.instructions.insert(first, list);
                } else
                    targetMethod.instructions.add(list);
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
                    if (currentLine == line) {
                        targetMethod.instructions.insertBefore(insnNode, list);
                        break;
                    }
                }
            } else if (at == At.AFTER_LINE) {
                for (AbstractInsnNode insnNode : targetMethod.instructions) {
                    if (!(insnNode instanceof LineNumberNode)) continue;
                    int currentLine = ((LineNumberNode) insnNode).line;
                    if (currentLine == line) {
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
                    if (currentLine == line) {
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