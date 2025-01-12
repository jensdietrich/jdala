package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair.ANNOTATION_TYPE;
import org.objectweb.asm.Label;
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
        if (opcode == Opcodes.ASTORE) {
            for (AnnotationPair pair : annotations) {
                if (pair.location.equals(classPath) && pair.index == varIndex && pair.annotation == ANNOTATION_TYPE.LOCAL) {
//                    System.out.println("New variable (" + pair.name + ") with @" + pair.annotation + " Index: " + varIndex);
                    injectThreadChecker(varIndex);
//                    break;
                }
//                injectThreadValidator(varIndex);
                break;
            }
        }
//        if (opcode == Opcodes.ALOAD) {
//            System.out.println(classPath + " " + varIndex);
////            injectThreadValidator(varIndex);
//        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor){
        if (opcode == Opcodes.PUTFIELD) {
//            System.out.println(owner);
            injectThreadValidator();
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }


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

    private void injectThreadValidator() {

        super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        super.visitLdcInsn("\t- System PrintLn injection Works");
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);


        // Load the variable onto the stack
//        super.visitInsn(Opcodes.DUP2);
//        super.visitInsn(Opcodes.POP);
//        super.visitInsn(Opcodes.POP);
//        super.visitInsn(Opcodes.DUP);

        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
                "printTest",
                "()V",
                false
        );

//        super.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
//                "validate",
//                "(Ljava/lang/Object;)V",
//                false
//        );

        // Call ThreadChecker.register

//        super.visitFieldInsn(Opcodes.GETSTATIC, "nz/ac/wgtn/ecs/jdala/ThreadChecker", "threadChecker", "Lnz/ac/wgtn/ecs/jdala/ThreadChecker;");
//
//        super.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "nz/ac/wgtn/ecs/jdala/ThreadChecker",
//                "printTest",
//                "()V",
//                false
//        );

//        injectThreadValidatorUsingReflection();
    }
//
//    private void injectThreadValidatorUsingReflection() {
//        // Try-Catch Block Setup
//        Label tryStart = new Label();
//        Label tryEnd = new Label();
//        Label catchBlock = new Label();
//
//        // Start of the try block
//        super.visitLabel(tryStart);
//
//        // Load the "nz.ac.wgtn.ecs.jdala.ThreadChecker" string onto the stack
//        super.visitLdcInsn("nz.ac.wgtn.ecs.jdala.ThreadChecker");
//
//        // Call Class.forName("nz.ac.wgtn.ecs.jdala.ThreadChecker")
//        super.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "java/lang/Class",
//                "forName",
//                "(Ljava/lang/String;)Ljava/lang/Class;",
//                false
//        );
//
//        // Duplicate the Class object to use it for the next operation
//        super.visitInsn(Opcodes.DUP);
//
//        // Load "printTest" (method name) onto the stack
//        super.visitLdcInsn("printTest");
//
//        // Push an empty array of Classes (no parameters for printTest)
//        super.visitInsn(Opcodes.ICONST_0);
//        super.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Class");
//
//        // Call Class.getMethod("printTest", Class<?>[] {})
//        super.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "java/lang/Class",
//                "getMethod",
//                "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
//                false
//        );
//
//        // Call Method.invoke(null, Object[] {})
//        super.visitInsn(Opcodes.ACONST_NULL); // No instance needed for static method
//        super.visitInsn(Opcodes.ICONST_0);
//        super.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
//        super.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "java/lang/reflect/Method",
//                "invoke",
//                "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
//                false
//        );
//
//        // End of the try block
//        super.visitLabel(tryEnd);
//
//        // Add the exception handler (catch block)
//        super.visitJumpInsn(Opcodes.GOTO, catchBlock);
//
//        // Exception handler block
//        super.visitLabel(catchBlock);
//        super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
//
//        // Print the stack trace of the exception
//        super.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "java/lang/Exception",
//                "printStackTrace",
//                "()V",
//                false
//        );
//    }

}
