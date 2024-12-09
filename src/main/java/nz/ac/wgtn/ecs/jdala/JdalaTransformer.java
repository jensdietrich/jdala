package nz.ac.wgtn.ecs.jdala;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class JdalaTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (className.startsWith("java/") || className.startsWith("org/") || className.startsWith("sun/") || className.startsWith("jdk/")) {
            return classfileBuffer;
        }

        try {
            ClassReader classReader = new ClassReader(classfileBuffer);
            SafeClassWriter classWriter = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_FRAMES);

            ClassVisitor classVisitor = new LocalClassVisitor(Opcodes.ASM9, classWriter, className);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }

    private static class LocalClassVisitor extends ClassVisitor {
        private final String className;
        public LocalClassVisitor(int api, ClassVisitor classVisitor, String className) {
            super(api, classVisitor);
            this.className = className;
        }

        @Override
        public MethodVisitor visitMethod(
                int access,
                String name,
                String descriptor,
                String signature,
                String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            String methodPath = className.replace('/', '.') + "." + name;

            Set<Integer> annotations = new HashSet<Integer>();

            AnnotationScannerMethodVisitor scanner = new AnnotationScannerMethodVisitor(Opcodes.ASM9, mv, methodPath);
            mv = scanner;

            return new BytecodeTransformerMethodVisitor(Opcodes.ASM9, mv, methodPath);
        }
    }

//    private static class LocalMethodVisitor extends MethodVisitor {
//        final String classPath;
//        private final HashSet<Integer> newVarsIndexes = new HashSet<>();
//        private final HashSet<Integer> reassignedIndexes = new HashSet<>();
////        private final HashSet<Integer> newVarsIndexes = new HashSet<>();
//
//
//        public LocalMethodVisitor(int api, MethodVisitor methodVisitor, String classPath) {
//            super(api, methodVisitor);
//            this.classPath = classPath;
//            System.out.println("New MethodVisitor: " + classPath);
//        }
//
////        @Override
////        public void visitTypeInsn(int opcode, String type) {
////            if (opcode == Opcodes.NEW) {
////                // Identify the created object
////                System.out.println("New object of type: " + type);
////
////                // Push a unique ID onto the stack (e.g., a hash or counter)
//////                super.visitLdcInsn(nextUniqueId()); // Use a counter or unique identifier generator
//////
//////                // Register the object with the unique ID
//////                super.visitMethodInsn(
//////                        Opcodes.INVOKESTATIC,
//////                        "nz/ac/wgtn/ecs/jdala/ThreadChecker",
//////                        "register",
//////                        "(Ljava/lang/Object;I)V", // Method signature: register(Object obj, int id)
//////                        false
//////                );
////            }
////            super.visitTypeInsn(opcode, type);
////        }
//
//
//        @Override
//        public void visitVarInsn(int opcode, int varIndex) {
//            super.visitMethodInsn(
//                    Opcodes.INVOKESTATIC,
//                    "nz/ac/wgtn/ecs/jdala/ThreadChecker",
//                    "printHiya",
//                    "()V",
//                    false
//            );
//            if (opcode == Opcodes.ASTORE) {
//                // Assigning a new value to a variable
//                if (!ThreadChecker.variableIdentityMap.containsKey(varIndex)) {
//                    // New variable
//                    String uniqueId = ThreadChecker.generateUniqueId();
//                    ThreadChecker.variableIdentityMap.put(varIndex, uniqueId);
//                    System.out.println("New variable tracked with ID: " + uniqueId + " Index: " + varIndex);
//                } else {
//                    // Reassignment
//                    System.out.println("Variable reassigned, retaining ID: " + ThreadChecker.variableIdentityMap.get(varIndex) + " Index: " + varIndex);
//                }
//            } else if (opcode == Opcodes.ALOAD) {
//                // Referencing a variable
//                if (ThreadChecker.variableIdentityMap.containsKey(varIndex)) {
//                    System.out.println("Variable accessed with ID: " + ThreadChecker.variableIdentityMap.get(varIndex) + " Index: " + varIndex);
//                }
//            }
//            super.visitVarInsn(opcode, varIndex);
//        }
//
//        @Override
//        public void visitInsn(int opcode) {
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
//            super.visitInsn(opcode);
//        }
//
//        @Override
//        public void visitLocalVariable(
//                String name,
//                String descriptor,
//                String signature,
//                Label start,
//                Label end,
//                int index) {
//            if (!name.equals("this")) {
//                System.out.println("Name: " + name + " Index: " + index + " Descriptor: " + descriptor.replace('/', '.'));
//            }
//            super.visitLocalVariable(name, descriptor, signature, start, end, index);
//        }
//
//        @Override
//        public AnnotationVisitor visitLocalVariableAnnotation(
//                int typeRef,
//                TypePath typePath,
//                Label[] start,
//                Label[] end,
//                int[] index,
//                String descriptor,
//                boolean visible) {
//
//            if (descriptor != null && descriptor.equals("Lnz/ac/wgtn/ecs/jdala/annotation/Local;")) {
//                for (int i : index) {
//                    System.out.println("Found @Local annotation on variable at index: " + i);
//                    injectThreadChecker(i);
//                }
//            }
//
//            return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
//        }
//
//        private void injectThreadChecker(int variableIndex) {
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
//        }
//
////        @Override
////        public void visitVarInsn(int opcode, int var) {
////            super.visitVarInsn(opcode, var);
////
////            // ThreadChecker.validate()
////            if (opcode == Opcodes.ALOAD) {
////                super.visitVarInsn(Opcodes.ALOAD, var);
////                super.visitMethodInsn(
////                        Opcodes.INVOKESTATIC,
////                        "nz/ac/wgtn/ecs/jdala/ThreadChecker",
////                        "validate",
////                        "(Ljava/lang/Object;)V",
////                        false);
////            }
////        }
//    }
}