package io.tunabytes.bytecode.introspect;

import io.tunabytes.ActualType;
import io.tunabytes.Definalize;
import io.tunabytes.EnumOverwrite;
import io.tunabytes.Mirror;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;

import java.util.HashSet;
import java.util.Set;

public class MixinFieldVisitor extends FieldVisitor {

    private static final Type MIRROR = Type.getType(Mirror.class);
    private static final Type DEFINALIZE = Type.getType(Definalize.class);
    private static final String ACTUAL_TYPE = Type.getDescriptor(ActualType.class);
    private static final String ENUM_OVERWRITE_TYPE = Type.getDescriptor(EnumOverwrite.class);

    protected boolean mirror, definalize, remapped;
    protected Type type;
    protected String name, desc, enumField;

    public MixinFieldVisitor(FieldNode node) {
        super(Opcodes.ASM8, node);
        desc = node.desc;
        enumField = node.name;
    }

    @Override public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        boolean mirrorAnn = MIRROR.getDescriptor().equals(descriptor);
        if (mirrorAnn)
            mirror = true;
        if (DEFINALIZE.getDescriptor().equals(descriptor))
            definalize = true;

        boolean isEnum1 = ENUM_OVERWRITE_TYPE.equals(descriptor);
        if (isEnum1) {
            remapped = true;
        }

        return new AnnotationVisitor(Opcodes.ASM8) {
            @Override public void visit(String name, Object value) {
                if (ACTUAL_TYPE.equals(descriptor)) {
                    remapped = true;
                    desc = MixinMethodVisitor.fromActualType(desc, (String) value).getDescriptor();
                }
                if (isEnum1 && name.equals("value")) {
                    if (value != null && ((String) value).length() > 0) {
                        enumField = (String) value;
                    }
                }

                if (mirrorAnn && name.equals("value")) {
                    MixinFieldVisitor.this.name = (String) value;
                }
            }
        };
    }

    @Override public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

}
