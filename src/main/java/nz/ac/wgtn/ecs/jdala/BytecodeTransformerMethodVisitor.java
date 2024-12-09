package nz.ac.wgtn.ecs.jdala;

import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

public class BytecodeTransformerMethodVisitor extends MethodVisitor {
    final String classPath;

    public BytecodeTransformerMethodVisitor(int api, MethodVisitor methodVisitor, String classPath) {
        super(api, methodVisitor);
        this.classPath = classPath;
//        System.out.println("New BytecodeTransformerMethodVisitor: " + classPath);
    }

//    @Override
//    public void visitVarInsn(int opcode, int varIndex) {
//        super.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
//                "printHiya",
//                "()V",
//                false
//        );
////        if (opcode == Opcodes.ASTORE) {
////            // Assigning a new value to a variable
////            if (!ThreadChecker.variableIdentityMap.containsKey(varIndex)) {
////                // New variable
////                String uniqueId = ThreadChecker.generateUniqueId();
////                ThreadChecker.variableIdentityMap.put(varIndex, uniqueId);
////                System.out.println("New variable tracked with ID: " + uniqueId + " Index: " + varIndex);
////            } else {
////                // Reassignment
////                System.out.println("Variable reassigned, retaining ID: " + ThreadChecker.variableIdentityMap.get(varIndex) + " Index: " + varIndex);
////            }
////        } else if (opcode == Opcodes.ALOAD) {
////            // Referencing a variable
////            if (ThreadChecker.variableIdentityMap.containsKey(varIndex)) {
////                System.out.println("Variable accessed with ID: " + ThreadChecker.variableIdentityMap.get(varIndex) + " Index: " + varIndex);
////            }
////        }
//        super.visitVarInsn(opcode, varIndex);
//    }
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
//    private void injectThreadChecker(int variableIndex) {
//
////            super.visitMethodInsn(
////                    Opcodes.INVOKESTATIC,
////                    "nz/ac/wgtn/ecs/jdala/ThreadChecker",
////                    "printHello",
////                    "()V",
////                    false
////            );
////            System.out.println("Hello");
////            // Load the variable onto the stack
////            super.visitVarInsn(Opcodes.ALOAD, variableIndex);
////            // Call ThreadChecker.register
////            super.visitMethodInsn(
////                    Opcodes.INVOKESTATIC,
////                    "nz/ac/wgtn/ecs/jdala/ThreadChecker",
////                    "register",
////                    "(Ljava/lang/Object;)V",
////                    false
////            );
//    }
}
