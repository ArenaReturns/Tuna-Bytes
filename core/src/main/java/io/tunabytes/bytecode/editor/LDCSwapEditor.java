package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.ClassNarrower;
import io.tunabytes.bytecode.InvalidMixinException;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LDCSwapEditor implements MixinsEditor {
    @Override
    public void edit(ClassNode originalClassNode, MixinInfo info) {
        for (MixinMethod method : info.getMixinMethods()) {
            List<MixinMethod.LdcSwapInfo> ldcSwaps = method.getLdcSwaps();
            if (ldcSwaps.isEmpty()) continue;

            for (MixinMethod.LdcSwapInfo ldcSwap : ldcSwaps) {
                MethodNode targetMethod = ClassNarrower.tryNarrow(originalClassNode, info, method);

                AtomicInteger ldcSkipped = new AtomicInteger(0);
                AtomicInteger applyCount = new AtomicInteger(0);

                for (AbstractInsnNode instruction : targetMethod.instructions) {
                    if (instruction.getOpcode() == Opcodes.LDC) {
                        if (applyCount.get() >= ldcSwap.getApplyCount() || (ldcSwap.getLdcSkipped() != -1 && ldcSkipped.get() < ldcSwap.getLdcSkipped())) {
                            ldcSkipped.incrementAndGet();
                            continue;
                        }

                        LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;

                        if (ldcSwap.getTargetLdc().equals(ldcInsnNode.cst))  {
                            ldcInsnNode.cst = ldcSwap.getNewLdc();
                            applyCount.incrementAndGet();
                        }
                    }
                }

                if (applyCount.get() != ldcSwap.getApplyCount()) {
                    throw new InvalidMixinException("LdcSwap ('" + ldcSwap.getTargetLdc() + "' => '"+ ldcSwap.getNewLdc() + "') was expected to apply " +
                                                    ldcSwap.getApplyCount() + ", but only applied " + applyCount.get() + " in " + info.getMixinName() + "#" +
                                                    method.getTargetMethodName());
                }
            }
        }
    }
}
