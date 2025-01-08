package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair.ANNOTATION_TYPE;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import java.util.Set;

public class LocalTransformerMethodVisitor extends MethodVisitor {
    final String classPath;
    final Set<AnnotationPair> annotations;

    public LocalTransformerMethodVisitor(MethodVisitor methodVisitor, Set<AnnotationPair> annotations, String classPath) {
        super(Opcodes.ASM9, methodVisitor);
        this.classPath = classPath;
        this.annotations = annotations;
//        System.out.println("New BytecodeTransformerMethodVisitor: " + classPath + " annotation size: " + annotations.size());
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        // Visit the instruction first because otherwise it won't exist yet
        super.visitVarInsn(opcode, varIndex);

        // TODO: Clean up so it doesn't have to loop through all annotated variables
        for (AnnotationPair pair : annotations) {
            if (opcode == Opcodes.ASTORE) {
                if (pair.location.equals(classPath) && pair.index == varIndex && pair.annotation == ANNOTATION_TYPE.LOCAL) {
                    System.out.println("New variable (" + pair.name + ") with @" + pair.annotation + " Index: " + varIndex);
                    injectThreadChecker(varIndex);
                    break;
                }
                injectThreadValidator(varIndex);
                break;
            }
        }
        if (opcode == Opcodes.ALOAD) {
//            System.out.println(classPath + " " + varIndex);
//            injectThreadValidator(varIndex);
        }
    }

//    @Override
//    public void visitFieldInsn(int opcode, String owner, String name, String descriptor){
//        if (opcode == Opcodes.PUTFIELD) {
//            injectThreadValidator(varIndex);
//        }
//    }


//    @Override
//    public void visitInsn(int opcode) {
//        super.visitInsn(opcode);
//    }

    private void injectThreadChecker(int varIndex) {
        // Load the variable onto the stack
        super.visitVarInsn(Opcodes.ALOAD, varIndex);
        // Call ThreadChecker.register
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                "registerLocal",
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
