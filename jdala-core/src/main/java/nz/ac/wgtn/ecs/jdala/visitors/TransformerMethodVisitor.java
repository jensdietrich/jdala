package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.*;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import java.util.Set;

/**
 * Injects calls to register methods at locations of  {@link nz.ac.wgtn.ecs.jdala.annotation.Immutable}, {@link nz.ac.wgtn.ecs.jdala.annotation.Isolated}, and {@link nz.ac.wgtn.ecs.jdala.annotation.Local}.
 * It also injects validate calls at each time PUTFIELD and GETFIELD are called.
 *
 * @author Quinten Smit
 */
public class TransformerMethodVisitor extends MethodVisitor {
    private final String classPath;
    private final String methodName;
    private final Set<AnnotationPair> annotations;
    private boolean superConstructorCalled = false;
    private final String superClassName;
    private final PortalMethod portalMethod;

    private int varCounter = 0;

    public TransformerMethodVisitor(MethodVisitor methodVisitor, String superClassName, Set<AnnotationPair> annotations, String classPath, String methodName, PortalMethod portalMethod) {
        super(Opcodes.ASM9, methodVisitor);
        this.superClassName = superClassName;
        this.classPath = classPath;
        this.methodName = methodName;
        this.annotations = annotations;
        this.portalMethod = portalMethod;

        if (!isConstructor()){
            this.superConstructorCalled = true;
        }

//        if (portalMethod != null){
//            System.out.println("I am a portal class" + classPath + " " + methodName + " of type " + portalMethod);
//        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (portalMethod != null && portalMethod.isExitPortal()) {
            injectStartExitPortal();
        }
    }

    /**
     * Each time that ASTORE is called a check is done to see if it is one of the annotated variables that were passed in
     */
    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        // Visit the instruction first because otherwise it won't exist yet
        super.visitVarInsn(opcode, varIndex);

        // TODO: Clean up so it doesn't have to loop through all annotated variables
        if (opcode == Opcodes.ASTORE) {
            for (AnnotationPair pair : annotations) {
                if (pair.location.equals(classPath + "." + methodName) && pair.index == varIndex) {
                    switch (pair.annotation) {
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

    /**
     * This is only used in the constructor, this will see if the super constructor is called and once it is it will inject validation code
     * for each of the PUTFIELDs and GETFIELDs that it couldn't do before. For more details on why this is needed please view the
     * <a href="https://github.com/jensdietrich/jdala/issues/5">GitHub Issue</a>
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>") && owner.equals(superClassName)) {
            superConstructorCalled = true;
            if (varCounter > 0){
                for (int var = 0; var < varCounter; var += 2) {

                    mv.visitVarInsn(Opcodes.ALOAD, 11 + var); // Load object
                    mv.visitVarInsn(Opcodes.ALOAD, 10 + var);  // Load value

                    injectWriteValidator();
                }
            }
        }
    }

    /**
     * Either inject calls to the writeValidator on PUTFIELD or store the values for calls after the super constructor has been called
     * Either inject calls to the readValidator on GETFIELD or store the values for calls after the super constructor has been called
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if ((descriptor.startsWith("L") || descriptor.startsWith("["))) {
            if (opcode == Opcodes.PUTFIELD) {
                if (superConstructorCalled) {
                    super.visitInsn(Opcodes.DUP2);
                    injectWriteValidator();
                } else {
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
                    super.visitInsn(Opcodes.DUP); // Field that is about to be read
                    injectReadValidator();
                }
            }
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitInsn(int opcode) {
        if (portalMethod != null){
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                if (portalMethod.isEntryPortal()) {
                    injectEntryPortal();
                }
                if (portalMethod.isExitPortal()) {
                    injectEndExitPortal(false);
                }
            } else if (opcode == Opcodes.ATHROW){ // TODO: Theoretically could allow a reference value that has been read from an isolated object escape via an exception
                injectEndExitPortal(true);
            }
        }
        if (opcode == Opcodes.AALOAD){ // Get from array
            super.visitInsn(Opcodes.DUP2);
            super.visitInsn(Opcodes.POP);
            injectReadValidator();
        } else if (opcode == Opcodes.AASTORE){ // Add to array
            super.visitInsn(Opcodes.DUP2_X1);// stack: arrayref, index, value → index, value, arrayref, index, value
            super.visitInsn(Opcodes.SWAP);   // stack: index, value, arrayref, index, value → index, value, arrayref, value, index
            super.visitInsn(Opcodes.POP);    // stack: index, value, arrayref, value, index → index, value, arrayref, value
            super.visitInsn(Opcodes.SWAP);   // stack: index, value, arrayref, value → index, value, value, arrayref
            super.visitInsn(Opcodes.DUP_X1); // stack: index, value, value, arrayref → index, value, arrayref, value, arrayref
            super.visitInsn(Opcodes.SWAP);   // stack: index, value, value, arrayref → index, value, arrayref, arrayref, value

            injectWriteValidator();
            mv.visitInsn(Opcodes.DUP_X2); // Stack: index, value, arrayref → arrayref, index, value, arrayref
            mv.visitInsn(Opcodes.POP);    // Stack: arrayref, index, value, arrayref → arrayref, index, value
        }

        super.visitInsn(opcode);
    }

    /**
     * Inject the register method call this can call any three of the register methods for Immutable, Isolated, or Local.
     * @param methodName The name of the method you want to call
     * @param varIndex The index of the variable that you want to register
     */
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

    /**
     * Inject a call to the JDala Write validator<br>
     * Note: this consumes the top two elements of the stack so load the elements to be consumed with opcodes like DUP2 or ALOAD
     */
    private void injectWriteValidator() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "validateWrite",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                false
        );
    }

    /**
     * Inject a call to the JDala Read validator<br>
     * Note: this consumes the top element of the stack so load the element to be consumed with opcodes like DUP or ALOAD
     */
    private void injectReadValidator() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "validateRead",
                "(Ljava/lang/Object;)V",
                false
        );
    }

    private void injectEntryPortal(){
        // TODO: Note that is might encounter the uninitialized this error if used on a constructor
        if (!superConstructorCalled){
            throw new RuntimeException("Not supported yet");
        }

        super.visitVarInsn(Opcodes.ALOAD, portalMethod.getParameterIndex() + 1);
        super.visitVarInsn(Opcodes.ALOAD, 0);

        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "enterPortal",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                false
        );
    }

    private void injectStartExitPortal(){
        // TODO: Note that is might encounter the uninitialized this error if used on a constructor
        if (!superConstructorCalled){
            throw new RuntimeException("Not supported yet");
        }

        super.visitVarInsn(Opcodes.ALOAD, 0);
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "startExitPortal",
                "(Ljava/lang/Object;)V",
                false
        );
    }

    private void injectEndExitPortal(boolean isError){
        // TODO: Note that is might encounter the uninitialized this error if used on a constructor
        if (!superConstructorCalled){
            throw new RuntimeException("Not supported yet");
        }

        if (isError){
            super.visitInsn(Opcodes.NULL);
        } else {
            super.visitInsn(Opcodes.DUP);
        }
        super.visitVarInsn(Opcodes.ALOAD, 0);
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "nz/ac/wgtn/ecs/jdala/JDala",
                "endExitPortal",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                false
        );
    }



    /**
     * Check if the current method is a constructor
     * @return true if constructor
     */
    public boolean isConstructor(){
        return methodName.equals("<init>");
    }
}