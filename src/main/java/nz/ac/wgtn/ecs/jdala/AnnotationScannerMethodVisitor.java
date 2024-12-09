package nz.ac.wgtn.ecs.jdala;

import org.objectweb.asm.*;

import java.util.HashSet;

public class AnnotationScannerMethodVisitor extends MethodVisitor{
    final String classPath;

    public AnnotationScannerMethodVisitor(int api, MethodVisitor methodVisitor, String classPath) {
        super(api, methodVisitor);
        this.classPath = classPath;
        System.out.println("New AnnotationScannerMethodVisitor: " + classPath);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                "printHello",
                "()V",
                false
        );
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitLocalVariable(
            String name,
            String descriptor,
            String signature,
            Label start,
            Label end,
            int index) {
        if (descriptor != null && !name.equals("this")) {
            System.out.println("Name: " + name + " Index: " + index + " Descriptor: " + descriptor.replace('/', '.'));
        }
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

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
            for (int i : index) {
                System.out.println("Found @Local annotation on variable at index: " + i);
            }
        }

        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
    }
}
