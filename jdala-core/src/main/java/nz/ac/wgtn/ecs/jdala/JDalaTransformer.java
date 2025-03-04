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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Once attached the JDalaTransformer runs the bytecode through two visitors, the first one being the {@link nz.ac.wgtn.ecs.jdala.visitors.AnnotationScannerClassVisitor} and the
 * second being the {@link TransformerClassVisitor} these check for annotations and modify bytecode accordingly.
 *
 * @author Quinten Smit
 */
public class JDalaTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {

            if (className.contains("shaded") || className.contains("java") || className.contains("sun")){
//                System.out.println("Skipping Shaded Class " + className);
                return classfileBuffer;
            }

            int lastSlashIndex = className.lastIndexOf('/');
            String result = className.substring(lastSlashIndex + 1);
            result = result.replace('$', '_');

            // Scan bytecode for annotations
            Set<AnnotationPair> annotations = Collections.newSetFromMap(new ConcurrentHashMap<>());
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassVisitor classVisitor = new AnnotationScannerClassVisitor(Opcodes.ASM9, annotations, className);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            // Edit bytecode to inject register, and validate calls
            SafeClassWriter classWriter = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_FRAMES);

            classVisitor = new TransformerClassVisitor(Opcodes.ASM9, classWriter, annotations, className);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            // TODO Remove next line debugging code
            Files.write(Paths.get("../generated-classes/" + result + ".class"), classWriter.toByteArray());

            return classWriter.toByteArray();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
            return classfileBuffer;
        }
    }
}