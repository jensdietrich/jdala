package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

/**
 * Class visitor for annotations, doesn't change much on ClassVisitor just calls {@link AnnotationScannerMethodVisitor} in the method visitor
 * and passes in the classname and annotations set
 */
public class AnnotationScannerClassVisitor extends ClassVisitor {
    private final String className;
    private final Set<AnnotationPair> annotations;
    public AnnotationScannerClassVisitor(int api, Set<AnnotationPair> annotations, String className) {
        super(api);
        this.className = className;
        this.annotations = annotations;
    }

    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        String methodPath = className.replace('/', '.') + "." + name;

        return new AnnotationScannerMethodVisitor(mv, annotations, methodPath);
    }
}
