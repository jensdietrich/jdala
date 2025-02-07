package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;

import nz.ac.wgtn.ecs.jdala.utils.CAPABILITY_TYPE;
import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Set;

/**
 * Finds annotations {@link nz.ac.wgtn.ecs.jdala.annotation.Immutable}, {@link nz.ac.wgtn.ecs.jdala.annotation.Isolated}, and {@link nz.ac.wgtn.ecs.jdala.annotation.Local}.
 * It then adds details about the variable locations so that once the Transformer Visitors visit they can inject register calls
 */
public class AnnotationScannerMethodVisitor extends MethodVisitor{
    final String classPath;
    Set<AnnotationPair> annotations;
    HashMap<Integer, String> varNames = new HashMap<>();

    public AnnotationScannerMethodVisitor(MethodVisitor methodVisitor, Set<AnnotationPair> annotations, String classPath) {
        super(Opcodes.ASM9, methodVisitor);
        this.classPath = classPath;
        this.annotations = annotations;
    }

    /**
     * Get variable names so that they can be linked to associated numbers later
     * note: that this step isn't necessary it just makes it easier to debug later on and means that more clear error messages can be given
     */
    @Override
    public void visitLocalVariable(
            String name,
            String descriptor,
            String signature,
            Label start,
            Label end,
            int index) {
        if (descriptor != null && !name.equals("this")) {
            varNames.put(index, name);
        }
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    /**
     * This call is one of the last run (also why it needs its own visitor, if it was run in the same as the Transformation we wouldn't know where the annotations are till after the bytecode has been edited). <br><br>
     * It will check if the annotations are one of these: <br>
     * {@link nz.ac.wgtn.ecs.jdala.annotation.Immutable}, {@link nz.ac.wgtn.ecs.jdala.annotation.Isolated}, or {@link nz.ac.wgtn.ecs.jdala.annotation.Local} <br>
     * and if they are then they are added to the {@link #annotations} set with the classpath and the index of the variables.
     * @return super annotation visitor call
     */
    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(
            int typeRef,
            TypePath typePath,
            Label[] start,
            Label[] end,
            int[] index,
            String descriptor,
            boolean visible) {

        if (descriptor != null) {
            CAPABILITY_TYPE capabilityType = null;

            switch (descriptor) {
                case "Lnz/ac/wgtn/ecs/jdala/annotation/Immutable;":
                    capabilityType = CAPABILITY_TYPE.IMMUTABLE;
                    break;
                case "Lnz/ac/wgtn/ecs/jdala/annotation/Isolated;":
                    capabilityType = CAPABILITY_TYPE.ISOLATED;
                    break;
                case "Lnz/ac/wgtn/ecs/jdala/annotation/Local;":
                    capabilityType = CAPABILITY_TYPE.LOCAL;
                    break;
                default:
                    System.out.println("Unknown annotation: " + descriptor);
            }

            // TODO: Check if needs to be changed later, might be unpredictable if there are annotations on the same line
            if (capabilityType != null) {
                for (int i : index) {
                    annotations.add(new AnnotationPair(i, capabilityType, classPath, varNames.get(i)));
                }
            }
        }

        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
    }
}
