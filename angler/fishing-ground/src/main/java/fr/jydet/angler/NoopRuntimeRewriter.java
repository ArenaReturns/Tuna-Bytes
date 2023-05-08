package fr.jydet.angler;

import static org.objectweb.asm.Opcodes.RETURN;

import io.tunabytes.Rewrite.Rewritter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class NoopRuntimeRewriter implements Rewritter {

  @Override
  public void rewrite(MethodNode methodVisitor) {
    methodVisitor.instructions = new InsnList();
    Label label0 = new Label();
    methodVisitor.visitLabel(label0);
    methodVisitor.visitLineNumber(22, label0);
    methodVisitor.visitInsn(RETURN);
    Label label1 = new Label();
    methodVisitor.visitLabel(label1);
    methodVisitor.visitLocalVariable("this", "Lfr/jydet/angler/mixintargets/SimplePOJO;", null, label0, label1, 0);
    methodVisitor.visitMaxs(0, 1);
  }
}
