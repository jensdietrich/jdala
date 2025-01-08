package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

class ImmutableTransformerClassVisitor extends ClassVisitor {
    private final String className;
    final Set<AnnotationPair> annotations;

    public ImmutableTransformerClassVisitor(int api, ClassVisitor classVisitor, Set<AnnotationPair> annotations, String className) {
        super(api, classVisitor);
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

        mv = new ImmutableTransformerMethodVisitor(mv, annotations, methodPath);
        return new LocalTransformerMethodVisitor(mv, annotations, methodPath);
    }

//        @Override
//        public void visitEnd(){
//            System.out.println("Done! Class: " + className);
//        }
}