package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumEditor implements MixinsEditor {

    private enum EnumOP {
        KEEP,
        REPLACE,
        ADD,
        DELETE
    }

    @Override
    public void edit(ClassNode node, MixinInfo info) {
        if (! info.isMixinEnum()) return;
        Map<String, EnumOP> enumsValues = new HashMap<>();

        for (String deletedEnumValue : info.getDeletedEnumValues()) {
            enumsValues.put(deletedEnumValue, EnumOP.DELETE);
        }

        for (MixinField field : info.getFields()) {
            if (field.isMirror()) continue;
            //don't add gcc generated field
            if (info.isMixinEnum() && field.getName().equals("$VALUES")) {
                continue;
            }

            //dont add fake enum field, we only care about the <clinit>
            if (field.getEnumField() == null) {
                String error = "Addition of field '" + field.getName() + "' in enum class '" + node.name + "' is not supported, did you forget @Mirror or @EnumOverwrite ?";
                System.err.println(error);
                throw new IllegalStateException(error);
            }
            if (node.fields.stream().noneMatch(existingField -> existingField.name.equals(field.getName()))) {
                if (enumsValues.containsKey(field.getName())) {
                    String error = "Addition of field '" + field.getName() + "' in enum class '" + node.name +
                            "' is not supported as an operation was already registered for it : '" + enumsValues.get(field.getName()) + "'";
                    System.err.println(error);
                    throw new IllegalStateException(error);
                }
                enumsValues.put(field.getName(), EnumOP.ADD);
                FieldNode addedEnumValue = field.getNode();
                addedEnumValue.access |= Opcodes.ACC_ENUM;
                addedEnumValue.desc = 'L' + node.name + ';';
                node.fields.add(addedEnumValue);
            } else {
                if (enumsValues.containsKey(field.getName())) {
                    String error = "Addition of field '" + field.getName() + "' in enum class '" + node.name +
                            "' is not supported as an operation was already registered for it : '" + enumsValues.get(field.getName()) + "'";
                    System.err.println(error);
                    throw new IllegalStateException(error);
                }
                enumsValues.put(field.getName(), EnumOP.REPLACE);
            }
        }

        node.fields.stream()
            .filter(fieldNode -> !enumsValues.containsKey(fieldNode.name))
            .filter(fieldNode -> (fieldNode.access & Opcodes.ACC_ENUM) == Opcodes.ACC_ENUM)
            .forEach(fieldNode -> enumsValues.put(fieldNode.name, EnumOP.KEEP));

        boolean enumConstructorFound = false;

        for (MixinMethod method : info.getMethods()) {
            if (method.getName().equals("<init>")) {
                Type[] argumentTypes = Type.getArgumentTypes(method.getRealDescriptor());
                if (argumentTypes.length > 2 &&
                        argumentTypes[1].getClassName().equals("int") &&
                        argumentTypes[0].getClassName().equals("java.lang.String")) {
                    enumConstructorFound = true;
                }
            } else
            if (method.getName().equals("<clinit>")) {
                MethodNode mixinClinit = method.getMethodNode();

                AbstractInsnNode last = mixinClinit.instructions.getLast();
                if (RETURN_OPCODES.contains(last.getOpcode())) {
                    mixinClinit.instructions.remove(last);
                } else {
                    throw new IllegalStateException("Was excepting a RETURN as last instruction of " + info.getMixinInternalName() + "'s <clinit> !");
                }

                MethodNode targetClinit = node.methods.stream()
                        .filter(m -> m.name.equals("<clinit>"))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("No <clinit> method found in target enum " + node.name));

                InsnList targetClinitInstructions = targetClinit.instructions;

                AbstractInsnNode targetClinitLast = targetClinitInstructions.getLast();

                AbstractInsnNode injectionPoint;

                while (true) {
                    if (targetClinitLast == null) {
                        throw new IllegalStateException("No init array instruction found in <clinit> of " + node.name);
                    }
                    if (targetClinitLast instanceof TypeInsnNode) {
                        TypeInsnNode typeInsnNode = (TypeInsnNode) targetClinitLast;
                        if (typeInsnNode.getOpcode() == Opcodes.ANEWARRAY && typeInsnNode.desc.equals(node.name)) {
                            injectionPoint = typeInsnNode;
                            break;
                        }
                    }

                    targetClinitLast = targetClinitLast.getPrevious();
                }
                AbstractInsnNode previous = injectionPoint;
                while (previous != null && ! (previous instanceof LabelNode)) {
                    previous = previous.getPrevious();
                }
                if (previous == null) {
                    throw new IllegalStateException("Unexpected enum bytecode");
                }
                last = targetClinitInstructions.getLast();
                while (last != previous) {
                    AbstractInsnNode tmp = last.getPrevious();
                    targetClinitInstructions.remove(last);
                    last = tmp;
                }

                AbstractInsnNode lastCopy = last;
                enumsValues.forEach((name, op) -> {
                    if (op == EnumOP.REPLACE || op == EnumOP.DELETE) {
                        AbstractInsnNode pointer = lastCopy;
                        //find the beginning of the <clinit> enum constant
                        while (true) {
                            if (pointer == null) {
                                throw new IllegalStateException("Pointer lost while " + op + "-ing enum '" + name + "' !");
                            }
                            if (pointer.getOpcode() == Opcodes.LDC) {
                                LdcInsnNode ldcInsnNode = (LdcInsnNode) pointer;
                                if (name.equals(ldcInsnNode.cst)) {
                                    break;
                                }
                            }
                            pointer = pointer.getPrevious();
                        }
                        while (true) {
                            if (pointer == null) {
                                throw new IllegalStateException("Pointer lost while " + op + "-ing enum '" + name + "' !");
                            }
                            if (pointer instanceof LabelNode) {
                                break;
                            }
                            pointer = pointer.getPrevious();
                        }
                        //remove all the instruction until the next enum declaration (next label node)
                        do {
                            AbstractInsnNode removePointer = pointer.getNext();
                            targetClinitInstructions.remove(pointer);
                            pointer = removePointer;
                        } while (! (pointer instanceof LabelNode));
                    }
                });

                targetClinitInstructions.insert(previous, mixinClinit.instructions);

                generateValuesCreation(targetClinit, node.name, new ArrayList<>(enumsValues.keySet()),
                        node.fields.stream()
                                .filter(fieldNode -> (fieldNode.access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC)
                                .map(fieldNode -> fieldNode.name)
                                .filter(name -> name.contains("$"))
                                .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No synthetic field found for enum " + node.name + "! Are you sure this is an enum ??")));

                for (AbstractInsnNode instruction : targetClinit.instructions) {
                    remapInstruction(node, info, instruction);
                }
            }
        }

        if (! enumConstructorFound) {
            throw new IllegalStateException("No enum constructor found in '" + info.getMixinInternalName() +
                    "'. You must provide a constructor with at least 2 parameters (String internalName, int internalId, ...)");
        }
    }

    private void generateValuesCreation(MethodVisitor mv, String parentName, List<String> enums, String synteticValuesFieldName) {
        //$Values size
        addIntToStack(mv, enums.size());
        mv.visitTypeInsn(Opcodes.ANEWARRAY, parentName);

        for (int i = 0; i < enums.size(); i++) {
            String enumValue = enums.get(i);
            mv.visitInsn(Opcodes.DUP);
            addIntToStack(mv, i);
            mv.visitFieldInsn(Opcodes.GETSTATIC, parentName, enumValue, "L" + parentName + ";");
            mv.visitInsn(Opcodes.AASTORE);
        }
        mv.visitFieldInsn(PUTSTATIC, parentName, synteticValuesFieldName, "[L" + parentName + ";");
        mv.visitInsn(Opcodes.RETURN);
    }

    private void addIntToStack(MethodVisitor mv, int value) {
        switch (value) {
            case 0: {
                mv.visitInsn(Opcodes.ICONST_0);
                break;
            }
            case 1: {
                mv.visitInsn(Opcodes.ICONST_1);
                break;
            }
            case 2: {
                mv.visitInsn(Opcodes.ICONST_2);
                break;
            }
            case 3: {
                mv.visitInsn(Opcodes.ICONST_3);
                break;
            }
            case 4: {
                mv.visitInsn(Opcodes.ICONST_4);
                break;
            }
            case 5: {
                mv.visitInsn(Opcodes.ICONST_5);
                break;
            }
            default: {
                mv.visitIntInsn(Opcodes.BIPUSH, value);
            }
        }
    }
}
