package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.SafeClassWriter;
import nz.ac.wgtn.ecs.jdala.visitors.AnnotationScannerClassVisitor;
import nz.ac.wgtn.ecs.jdala.visitors.TransformerClassVisitor;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.*;

public class JDalaTransformer implements ClassFileTransformer {
    private static int count = 0;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {
            int lastSlashIndex = className.lastIndexOf('/');
            String result = className.substring(lastSlashIndex + 1);
            result = result.replace('$', '_');
//            System.out.println(count + " Current Class: " + result);

            // Scan bytecode
            // TODO: Replace with thread safe collection
            Set<AnnotationPair> annotations = new HashSet<>();
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassVisitor classVisitor = new AnnotationScannerClassVisitor(Opcodes.ASM9, annotations, className);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            // Edit bytecode
//            classReader = new ClassReader(classfileBuffer);
            SafeClassWriter classWriter = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_FRAMES);

            classVisitor = new TransformerClassVisitor(Opcodes.ASM9, classWriter, annotations, className);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            Files.write(Paths.get("generated-classed/" + result + ".class"), classWriter.toByteArray());
            count++;

            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }
}