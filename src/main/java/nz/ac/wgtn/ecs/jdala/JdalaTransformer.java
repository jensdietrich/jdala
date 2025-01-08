package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.*;

public class JdalaTransformer implements ClassFileTransformer {
    private static int count = 0;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {
//            int lastSlashIndex = className.lastIndexOf('/');
//            String result = className.substring(lastSlashIndex + 1);
//            result = result.replace('$', '.');
//            System.out.println(count + result);

            // Scan bytecode
            // TODO: Replace with thread safe collection
            Set<AnnotationPair> annotations = new HashSet<>();
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassVisitor classVisitor = new ScannerClassVisitor(Opcodes.ASM9, annotations, className);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            // Edit bytecode
//            classReader = new ClassReader(classfileBuffer);
            SafeClassWriter classWriter = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_FRAMES);

            classVisitor = new TransformerClassVisitor(Opcodes.ASM9, classWriter, annotations, className);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

//            Files.write(Paths.get(count++ + result +"-t.class"), classWriter.toByteArray());

            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }

    private static class ScannerClassVisitor extends ClassVisitor {
        private final String className;
        private final Set<AnnotationPair> annotations;
        public ScannerClassVisitor(int api, Set<AnnotationPair> annotations,String className) {
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

    private static class TransformerClassVisitor extends ClassVisitor {
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

            mv = new ImmutableTransformerMethodVisitor(mv, annotations, methodPath);
            return new LocalTransformerMethodVisitor(mv, annotations, methodPath);
        }

//        @Override
//        public void visitEnd(){
//            System.out.println("Done! Class: " + className);
//        }
    }
}