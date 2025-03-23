package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.PortalClass;
import nz.ac.wgtn.ecs.jdala.utils.PortalMethod;
import nz.ac.wgtn.ecs.jdala.utils.SafeClassWriter;
import nz.ac.wgtn.ecs.jdala.visitors.AnnotationScannerClassVisitor;
import nz.ac.wgtn.ecs.jdala.visitors.TransformerClassVisitor;
import org.objectweb.asm.*;
import shaded.org.json.JSONArray;
import shaded.org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Once attached the JDalaTransformer runs the bytecode through two visitors, the first one being the {@link nz.ac.wgtn.ecs.jdala.visitors.AnnotationScannerClassVisitor} and the
 * second being the {@link TransformerClassVisitor} these check for annotations and modify bytecode accordingly.
 *
 * @author Quinten Smit
 */
public class JDalaTransformer implements ClassFileTransformer {

    private final Set<PortalClass> portalClasses = new HashSet<>();

    public JDalaTransformer(){
        loadPortalClasses();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {

            if (className.contains("shaded")){
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

            classVisitor = new TransformerClassVisitor(Opcodes.ASM9, classWriter, annotations, className, portalClasses);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            // TODO Remove next line debugging code
            // Files.write(Paths.get("../generated-classes/" + result + ".class"), classWriter.toByteArray());

            return classWriter.toByteArray();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
            return classfileBuffer;
        }
    }

    /**
     * Loads the portal class file
     */
    private void loadPortalClasses() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("portal-classes.json")) {

            if (is == null) {
                throw new IllegalStateException("portal-classes.json not found");
            }

            // Read JSON file into a string
            String jsonText;
            try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
                jsonText = scanner.useDelimiter("\\A").next();
            }

            JSONObject rootNode = new JSONObject(jsonText);
            JSONArray classesArray = rootNode.optJSONArray("classes");

            if (classesArray == null) {
                throw new IllegalStateException("Invalid JSON: 'classes' field is missing or not an array.");
            }

            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject classObject = classesArray.getJSONObject(i);

                Set<PortalMethod> portalMethods = Stream.concat(
                    classObject.getJSONArray("entryMethods").toList().stream()
                    .map(obj -> {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String methodName = (String) map.get("methodName");
                        int parameterIndex = (int) map.getOrDefault("parameterIndex", -1);
                        String descriptor = (String) map.getOrDefault("descriptor", "");
                        return new PortalMethod(methodName, parameterIndex, descriptor, true);
                    }),
                    classObject.getJSONArray("exitMethods").toList().stream()
                    .map(obj -> {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String methodName = (String) map.get("methodName");
                        String descriptor = (String) map.getOrDefault("descriptor", "");
                        return new PortalMethod(methodName, descriptor, false);
                    })).collect(Collectors.toSet());

                PortalClass portalClass = new PortalClass(
                        classObject.getString("className"),
                        portalMethods,
                        classObject.optBoolean("includeSubClasses", false)
                );
                portalClasses.add(portalClass);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load portal classes", e);
        }
    }
}