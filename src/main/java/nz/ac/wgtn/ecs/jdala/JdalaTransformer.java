package nz.ac.wgtn.ecs.jdala;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.*;

public class JdalaTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (className.startsWith("java/") || className.startsWith("org/")) {
            return classfileBuffer;
        }

        try {
            ClassReader classReader = new ClassReader(classfileBuffer);
            SafeClassWriter classWriter = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_FRAMES);

            ClassVisitor classVisitor = new LocalClassVisitor(Opcodes.ASM9, classWriter);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }

    private static class LocalClassVisitor extends ClassVisitor {
        public LocalClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(
                int access,
                String name,
                String descriptor,
                String signature,
                String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new LocalMethodVisitor(Opcodes.ASM9, mv);
        }
    }

    private static class LocalMethodVisitor extends MethodVisitor {
        public LocalMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitLocalVariable(
                String name,
                String descriptor,
                String signature,
                Label start,
                Label end,
                int index) {
            super.visitLocalVariable(name, descriptor, signature, start, end, index);
        }

//        @Override
//        public void visitLineNumber(int line, Label start) {
//            super.visitLineNumber(line, start);
//        }
        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(
                int typeRef,
                TypePath typePath,
                Label[] start,
                Label[] end,
                int[] index,
                String descriptor,
                boolean visible) {

            if (descriptor != null && descriptor.equals("Lnz/ac/wgtn/ecs/jdala/annotation/Local;")) {
                System.out.println("Found @Local annotation on variable at index: " + index[0]);
                for (int i : index) {
                    injectThreadChecker(i);
                }
            }

            return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
        }

        private void injectThreadChecker(int variableIndex) {
            // Load the variable onto the stack
            super.visitVarInsn(Opcodes.ALOAD, variableIndex);
            // Call ThreadChecker.register
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                    "register",
                    "(Ljava/lang/Object;)V",
                    false
            );
        }

//        @Override
//        public void visitVarInsn(int opcode, int var) {
//            super.visitVarInsn(opcode, var);
//
//            // ThreadChecker.validate()
//            if (opcode == Opcodes.ALOAD) {
//                super.visitVarInsn(Opcodes.ALOAD, var);
//                super.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        "nz/ac/wgtn/ecs/jdala/ThreadChecker",
//                        "validate",
//                        "(Ljava/lang/Object;)V",
//                        false);
//            }
//        }
    }
}