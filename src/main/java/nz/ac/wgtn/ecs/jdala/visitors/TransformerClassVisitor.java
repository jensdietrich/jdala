package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;

public class TransformerClassVisitor extends ClassVisitor {
    private final String className;
    final Set<AnnotationPair> annotations;

    public TransformerClassVisitor(int api, ClassVisitor classVisitor, Set<AnnotationPair> annotations, String className) {
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

        if (access == Opcodes.ACC_NATIVE){
            System.out.println("Native Method: " + methodPath);
            return mv;
        }

        return new AnnotationTransformerMethodVisitor(mv, annotations, methodPath);
    }

//        @Override
//        public void visitEnd(){
//            System.out.println("Done! Class: " + className);
//        }
}