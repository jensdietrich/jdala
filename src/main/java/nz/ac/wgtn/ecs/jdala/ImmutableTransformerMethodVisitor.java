package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair.ANNOTATION_TYPE;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;

public class ImmutableTransformerMethodVisitor extends MethodVisitor {
    final String classPath;
    final Set<AnnotationPair> annotations;

    public ImmutableTransformerMethodVisitor(MethodVisitor methodVisitor, Set<AnnotationPair> annotations, String classPath) {
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
                if (pair.location.equals(classPath) && pair.index == varIndex && pair.annotation == ANNOTATION_TYPE.IMMUTABLE) {
                    System.out.println("New variable (" + pair.name + ") with @" + pair.annotation + " Index: " + varIndex);
                    injectThreadChecker(varIndex);
                }
//                injectThreadValidator(varIndex);
                break;
            }
        }
    }


    private void injectThreadChecker(int varIndex) {
        super.visitVarInsn(Opcodes.ALOAD, varIndex);
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                "registerLocal",
                "(Ljava/lang/Object;)V",
                false
        );
    }

    private void injectThreadValidator(int varIndex) {
        super.visitVarInsn(Opcodes.ALOAD, varIndex);
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                "validate",
                "(Ljava/lang/Object;)V",
                false
        );
    }
}
