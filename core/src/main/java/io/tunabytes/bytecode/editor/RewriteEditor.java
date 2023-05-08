package io.tunabytes.bytecode.editor;

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
  public void edit(ClassNode classNode, MixinInfo info) {
      for (MixinMethod method : info.getMethods()) {
        if (!method.isRewrite()) continue;
        if ((method.getMethodNode().access & ACC_ABSTRACT) == 0) {
          throw new IllegalArgumentException("@Rewrite must be used on abstract methods! (" + method.getMethodNode().name + " in " + info.getMixinName() + ")");
        }
        MethodNode node = method.getMethodNode();
        MethodNode underlying = classNode.methods.stream()
            .filter(c -> c.name.equals(method.getOverwrittenName()) && c.desc.equals(node.desc))
            .findFirst().orElseThrow(() -> new NoSuchMethodException(method.getOverwrittenName()));
        
        try {
          method.getRewritter().newInstance().rewrite(underlying);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        
        for (AbstractInsnNode instruction : underlying.instructions) {
          remapInstruction(classNode, info, instruction);
        }
      }
    }
}
