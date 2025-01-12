package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import static nz.ac.wgtn.ecs.jdala.utils.AnnotationPair.ANNOTATION_TYPE;
import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Set;


public class AnnotationScannerMethodVisitor extends MethodVisitor{
    final String classPath;
    Set<AnnotationPair> annotations;
    HashMap<Integer, String> varNames = new HashMap<>();

    public AnnotationScannerMethodVisitor(MethodVisitor methodVisitor, Set<AnnotationPair> annotations, String classPath) {
        super(Opcodes.ASM9, methodVisitor);
        this.classPath = classPath;
        this.annotations = annotations;
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
            varNames.put(index, name);
//            System.out.println("Name: " + name + " Index: " + index + " Descriptor: " + descriptor.replace('/', '.'));
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

        if (descriptor != null) {
            ANNOTATION_TYPE annotationType = null;

            switch (descriptor) {
                case "Lnz/ac/wgtn/ecs/jdala/annotation/Immutable;":
                    annotationType = ANNOTATION_TYPE.IMMUTABLE;
                    break;
                case "Lnz/ac/wgtn/ecs/jdala/annotation/Isolated;":
                    annotationType = ANNOTATION_TYPE.ISOLATED;
                    break;
                case "Lnz/ac/wgtn/ecs/jdala/annotation/Local;":
                    annotationType = ANNOTATION_TYPE.LOCAL;
                    break;
                default:
                    System.out.println("Unknown annotation: " + descriptor);
            }

            // TODO: Check if needs to be changed later, might be unpredictable if there are annotations on the same line
            if (annotationType != null) {
                for (int i : index) {
                    System.out.println("AnnotationScannerMethodVisitor: " + classPath);
                    System.out.println("Found @" + annotationType + " annotation on variable (" + varNames.get(i) + ") at index: " + i);
                    annotations.add(new AnnotationPair(i, annotationType, classPath, varNames.get(i)));
                }
            }
        }

        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
    }
}
