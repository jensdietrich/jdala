package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

/**
 * Class visitor for transformation, the main function is to calls {@link TransformerMethodVisitor} in the method visitor
 * and passes in the classname, annotations, and the superClassName.
 *
 * @author Quinten Smit
 */
public class TransformerClassVisitor extends ClassVisitor {
    private final String className;
    final Set<AnnotationPair> annotations;
    private String superClassName;

    public TransformerClassVisitor(int api, ClassVisitor classVisitor, Set<AnnotationPair> annotations, String className) {
        super(api, classVisitor);
        this.className = className;
        this.annotations = annotations;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superClassName = superName;
        super.visit(version, access, name, signature, superName, interfaces);
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

        return new TransformerMethodVisitor(mv, superClassName, annotations, methodPath);
    }
}