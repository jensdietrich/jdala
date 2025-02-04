package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.CAPABILITY_TYPE;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import java.util.Set;


public class TransformerMethodVisitor extends MethodVisitor {
    private final String classPath;
    private final Set<AnnotationPair> annotations;
    private boolean superConstructorCalled = false;
    private final String superClassName;
    private final String methodDescriptor;

    private int varCounter = 0;

    public TransformerMethodVisitor(MethodVisitor methodVisitor, String superClassName, String descriptor, Set<AnnotationPair> annotations, String classPath) {
        super(Opcodes.ASM9, methodVisitor);
        this.superClassName = superClassName;
        this.classPath = classPath;
        this.annotations = annotations;

        this.methodDescriptor = descriptor;
        int count = methodDescriptor.length() - methodDescriptor.replace(";", "").length();

        if (!isConstructor()){
            this.superConstructorCalled = true;
        }

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
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>") && owner.equals(superClassName)) {
            superConstructorCalled = true;
            if (varCounter > 0){
                for (int var = 0; var < varCounter; var += 2) {

                    mv.visitVarInsn(Opcodes.ALOAD, 11 + var); // Load object
                    mv.visitVarInsn(Opcodes.ALOAD, 10 + var);  // Load value

                    super.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            "nz/ac/wgtn/ecs/jdala/JDala",
                            "validateWrite",
                            "(Ljava/lang/Object;Ljava/lang/Object;)V",
                            false
                    );
                }
            }
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if ((descriptor.startsWith("L") || descriptor.startsWith("["))) {
            if (opcode == Opcodes.PUTFIELD) {
                if (superConstructorCalled) {
                    injectWriteValidator();
                } else {
                    System.out.println(classPath + " " + name + " " + descriptor + " " + varCounter);

                    // Store the value (assuming it's an Object)
                    mv.visitVarInsn(Opcodes.ASTORE, 10 + varCounter);
                    // Store the object reference
                    mv.visitVarInsn(Opcodes.ASTORE, 11 + varCounter);


                    mv.visitVarInsn(Opcodes.ALOAD, 11 + varCounter);
                    mv.visitVarInsn(Opcodes.ALOAD, 10 + varCounter);
                    varCounter += 2;
                }
            } else if (opcode == Opcodes.GETFIELD) { // Needs to be added after field has been retrieved
                if (superConstructorCalled) {
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                    injectReadValidator();
                    return;
                }
            }
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
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

    private void injectWriteValidator() {
        super.visitInsn(Opcodes.DUP2);

        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "validateWrite",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                false
        );
    }

    private void injectReadValidator() {
        super.visitInsn(Opcodes.DUP);

        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "validateRead",
                "(Ljava/lang/Object;)V",
                false
        );
    }

    private void injectConstructorValidator() {
        super.visitVarInsn(Opcodes.ALOAD, 0);
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "validateConstructor",
                "(Ljava/lang/Object;)V",
                false
        );
    }

    public boolean isConstructor(){
        return classPath.endsWith("<init>");
    }
}