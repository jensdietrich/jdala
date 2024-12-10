package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BytecodeTransformerMethodVisitor extends MethodVisitor {
    final String classPath;
    final Set<AnnotationPair> annotations;

    private Map<Integer, String> variableIdentityMap = new HashMap<>();
    private int counter = 0;

    private String previousWasReAlias = null;

    public String generateUniqueId() {
        return "VarID-" + (counter++);
    }

    public BytecodeTransformerMethodVisitor(MethodVisitor methodVisitor, Set<AnnotationPair> annotations, String classPath) {
        super(Opcodes.ASM9, methodVisitor);
        this.classPath = classPath;
        this.annotations = annotations;
        System.out.println("New BytecodeTransformerMethodVisitor: " + classPath + " annotation size: " + annotations.size());
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        // Visit the instruction first because otherwise it won't exist yet
        super.visitVarInsn(opcode, varIndex);

        // TODO: Clean up so it doesn't have to loop through all annotated variables
        for (AnnotationPair pair : annotations) {
            if (opcode == Opcodes.ASTORE) {
                // Assigning a new value to a variable
                if (pair.location.equals(classPath) && pair.index == varIndex) {
                    if (!variableIdentityMap.containsKey(varIndex)) {
                        // New variable
                        String uniqueId = generateUniqueId();
                        variableIdentityMap.put(varIndex, uniqueId);
                        System.out.println("New variable (" + pair.name + ") with @" + pair.annotation + " tracked with ID: " + uniqueId + " Index: " + varIndex);
                        injectThreadChecker(varIndex);
                    } else {
                        // Reassignment
                        System.out.println("Variable (" + pair.name + ") reassigned, forgetting ID: " + variableIdentityMap.get(varIndex) + " Index: " + varIndex);
                        variableIdentityMap.remove(varIndex);
                    }
                } else if (previousWasReAlias != null) { // TODO: current implementation doesn't factor in if there has been a method called, will likely need to replace with Stack to keep track of all loaded variables
                    variableIdentityMap.put(varIndex, previousWasReAlias);
                    System.out.println("Reassigned variable (" + pair.name + ") tracked with same ID: " + previousWasReAlias + " Index: " + varIndex);
//                    injectThreadValidator(varIndex);
                    previousWasReAlias = null;
                }
                break;

            } else if (opcode == Opcodes.ALOAD) {
                // Referencing a variable
                if (variableIdentityMap.containsKey(varIndex)) {
                    System.out.println("Variable (" + pair.name + ") with @" + pair.annotation + " accessed with ID: " + variableIdentityMap.get(varIndex) + " Index: " + varIndex);
                    injectThreadValidator(varIndex);
                    previousWasReAlias = variableIdentityMap.get(varIndex);
                    return;
                }
            }
        }
        previousWasReAlias = null;

    }
//
//    @Override
//    public void visitInsn(int opcode) {
////            if (opcode == Opcodes.DUP) {
////                // Handle aliasing: propagate identity from one variable to another
////                int sourceIndex = ...; // Get the source variable index
////                int targetIndex = ...; // Get the target variable index
////                if (ThreadChecker.variableIdentityMap.containsKey(sourceIndex)) {
////                    String sourceId = ThreadChecker.variableIdentityMap.get(sourceIndex);
////                    ThreadChecker.variableIdentityMap.put(targetIndex, sourceId);
////                    System.out.println("Aliased variable with ID: " + sourceId);
////                }
////            }
//        super.visitInsn(opcode);
//    }
//
    private void injectThreadChecker(int varIndex) {
        // Load the variable onto the stack
        super.visitVarInsn(Opcodes.ALOAD, varIndex);
        // Call ThreadChecker.register
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                "register",
                "(Ljava/lang/Object;)V",
                false
        );
    }

    private void injectThreadValidator(int varIndex) {
        // Load the variable onto the stack
        super.visitVarInsn(Opcodes.ALOAD, varIndex);
        // Call ThreadChecker.register
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                "validate",
                "(Ljava/lang/Object;)V",
                false
        );
    }
}
