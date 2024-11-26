package io.tunabytes.bytecode.introspect;

import io.tunabytes.Inject.At;
import io.tunabytes.Rewrite.Rewritter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

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

    public enum CallType {
        INVOKE,
        GET,
        SET
    }

    public boolean isPrivate() {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }

}
