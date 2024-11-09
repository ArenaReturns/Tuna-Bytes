package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.ClassNarrower;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * A mixins editor for processing {@link io.tunabytes.Rewrite} methods.
 */
public class RewriteEditor implements MixinsEditor {

  @Override
  @SneakyThrows
  public void edit(ClassNode originalClassNode, MixinInfo info) {
      for (MixinMethod mixinMethod : info.getMixinMethods()) {
        if (!mixinMethod.isRewrite()) continue;
        MethodNode underlying = ClassNarrower.tryNarrow(originalClassNode, info, mixinMethod);
        
        try {
          mixinMethod.getRewritter().newInstance().rewrite(underlying);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        
        for (AbstractInsnNode instruction : underlying.instructions) {
          remapInstruction(originalClassNode, info, instruction);
        }
      }
    }
}
