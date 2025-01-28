package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.CAPABILITY_TYPE;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import java.util.Set;

public class TransformerMethodVisitor extends MethodVisitor {
    final String classPath;
    final Set<AnnotationPair> annotations;
    private boolean hasPutField = false;

    public TransformerMethodVisitor(MethodVisitor methodVisitor, Set<AnnotationPair> annotations, String classPath) {
        super(Opcodes.ASM9, methodVisitor);
        this.classPath = classPath;
        this.annotations = annotations;
//        System.out.println("New TransformerMethodVisitor: " + classPath + " annotation size: " + annotations.size());
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
                        case CAPABILITY_TYPE.IMMUTABLE:
                            injectRegister("registerImmutable", varIndex);
                            break;
                        case CAPABILITY_TYPE.ISOLATED:
                            injectRegister("registerIsolated", varIndex);
                            break;
                        case CAPABILITY_TYPE.LOCAL:
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
//        System.out.println(descriptor);
        if (opcode == Opcodes.PUTFIELD) {
//            System.out.println(owner + " " + name + " " + descriptor);
            if ((descriptor.startsWith("L") || descriptor.startsWith("[")) && !isConstructor()) {
                injectValidator();
            }
            hasPutField = true;
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN && isConstructor() && hasPutField) {
            injectConstructorValidator();
        }
        super.visitInsn(opcode);
    }

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
        super.visitInsn(Opcodes.DUP2);

        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "validate",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                false
        );
    }

    private void injectConstructorValidator() {
//        super.visitVarInsn(Opcodes.ALOAD, 0);
//        super.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "nz/ac/wgtn/ecs/jdala/JDala",
//                "validateConstructor",
//                "(Ljava/lang/Object;Ljava/lang/Object;)V",
//                false
//        );
    }


    public boolean isConstructor(){
        return classPath.endsWith("<init>");
    }
}