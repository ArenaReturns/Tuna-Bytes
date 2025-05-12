package io.tunabytes.bytecode.introspect;

import io.tunabytes.Inject.At;
import io.tunabytes.Rewrite.Rewritter;
import io.tunabytes.bytecode.InvalidMixinException;
import lombok.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public final class MixinMethod {

    private final String targetMethodName;
    private final int access;
    private final Type descriptor;
    private final String realDescriptor;
    private final int injectLine;
    private final int injectLineReplaceEnd;
    private final int manualInstructionSkip;
    private final int lastParameterArgIndex;
    private final At injectAt;
    private final boolean overwrite, rewrite, accessor, inject, mirror, definalize, requireTypeRemapping, keepLastReturn;
    private final Class<? extends Rewritter> rewritter;
    private final String accessedProperty; // or accessed method
    private final MethodNode methodNode;
    private final CallType type;
    private final boolean localCapture;

    private final List<LdcSwapInfo> ldcSwaps;

    @Getter
    @AllArgsConstructor
    public static final class LdcSwapInfo {
        private final String targetLdc, newLdc;
        private final int ldcSkipped, applyCount;

        public static LdcSwapInfoBuilder builder() {
            return new LdcSwapInfoBuilder();
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class LdcSwapInfoBuilder {
            private String targetLdc, newLdc;
            private int ldcSkipped = 0;
            private int applyCount = 1;

            public LdcSwapInfoBuilder ldcSkipped(int ldcSkipped) {
                this.ldcSkipped = ldcSkipped;
                return this;
            }

            public LdcSwapInfoBuilder applyCount(int applyCount) {
                this.applyCount = applyCount;
                return this;
            }

            public LdcSwapInfoBuilder targetLdc(String targetLdc) {
                this.targetLdc = targetLdc;
                return this;
            }

            public LdcSwapInfoBuilder newLdc(String newLdc) {
                this.newLdc = newLdc;
                return this;
            }

            public LdcSwapInfo build() throws IllegalStateException {
                if (targetLdc == null) {
                    throw new IllegalStateException("@LdcSwap targetLdc must be specified");
                }
                if (newLdc == null) {
                    throw new IllegalStateException("@LdcSwap newLdc must be specified");
                }
                if (ldcSkipped < 0) {
                    throw new IllegalStateException("@LdcSwap ldcSkipped must be >= 0");
                }
                if (applyCount < 0) {
                    throw new IllegalStateException("@LdcSwap applyCount must be > 0");
                }
                return new LdcSwapInfo(targetLdc, newLdc, ldcSkipped, applyCount);
            }
        }
    }


    public enum CallType {
        INVOKE,
        GET,
        SET
    }

    public boolean isPrivate() {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }

}
