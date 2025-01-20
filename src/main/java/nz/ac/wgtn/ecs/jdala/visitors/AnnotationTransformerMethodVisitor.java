package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair.ANNOTATION_TYPE;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import java.util.Set;

public class AnnotationTransformerMethodVisitor extends MethodVisitor {
    final String classPath;
    final Set<AnnotationPair> annotations;

    public AnnotationTransformerMethodVisitor(MethodVisitor methodVisitor, Set<AnnotationPair> annotations, String classPath) {
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
        if (opcode == Opcodes.ASTORE) {
            for (AnnotationPair pair : annotations) {
                if (pair.location.equals(classPath) && pair.index == varIndex) {
                    switch (pair.annotation){
                        case ANNOTATION_TYPE.IMMUTABLE:
                            injectRegister("registerImmutable", varIndex);
                            break;
                        case ANNOTATION_TYPE.ISOLATED:
                            injectRegister("registerIsolated", varIndex);
                            break;
                        case ANNOTATION_TYPE.LOCAL:
                            injectRegister("registerLocal", varIndex);
                            break;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (opcode == Opcodes.PUTFIELD) {
            injectValidator();
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }


//    @Override
//    public void visitInsn(int opcode) {
//        super.visitInsn(opcode);
//    }

    private void injectRegister(String methodName, int varIndex) {
        // Load the variable onto the stack
        super.visitVarInsn(Opcodes.ALOAD, varIndex);
        // Call JDala.register_____
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                methodName,
                "(Ljava/lang/Object;)V",
                false
        );
    }

    private void injectValidator() {

//        super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//        super.visitLdcInsn("\t- System PrintLn injection Works");
//        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);


        // Load the variable onto the stack
        super.visitInsn(Opcodes.DUP2);
        super.visitInsn(Opcodes.POP2);
//        super.visitInsn(Opcodes.POP);
//        super.visitInsn(Opcodes.DUP);

//        super.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "nz/ac/wgtn/ecs/jdala/JDala",
//                "printTest",
//                "()V",
//                false
//        );

//        super.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "nz/ac/wgtn/ecs/jdala/JDala",
//                "validate",
//                "(Ljava/lang/Object;)V",
//                false
//        );
    }
}