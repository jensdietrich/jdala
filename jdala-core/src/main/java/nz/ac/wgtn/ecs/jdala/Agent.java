package nz.ac.wgtn.ecs.jdala;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

/**
 * Java Agent entry point, executed before the main application starts.
 * This agent enhances the application's bytecode at runtime by attaching the {@link JDalaTransformer} to the {@link Instrumentation} instance.
 * It also loads additional classes from the JDala agent JAR into the bootstrap class loader, enabling transformation of loaded classes.
 *
 * @author Quinten Smit
 */
public class Agent {

    /**
     * Run before any other code is loaded
     * Attach JDala and add {@link JDalaTransformer}
     */
    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        debugCode();
        File file = new File("target/jdala-agent.jar");
        if (!file.exists()) {
            throw new IllegalStateException("file not found: " + file.getAbsolutePath());
        }
        JarFile jarFile = new JarFile(file);
        inst.appendToBootstrapClassLoaderSearch(jarFile);
        System.out.println("Starting agent");
        inst.addTransformer(new JDalaTransformer(), true);

        try {
            for (Class<?> clazz : inst.getAllLoadedClasses()) {
                if (((clazz.getName().startsWith("java.util.") && !clazz.getName().startsWith("java.util.concurrent.locks")) ||
                        clazz.getName().startsWith("java.nio.") ||
                        (clazz.getName().startsWith("java.lang.") && !(clazz.getName().equals("java.lang.ref.ReferenceQueue") ||
                        clazz.getName().equals("java.lang.invoke.BoundMethodHandle$Specializer") || clazz.getName().equals("java.lang.invoke.LambdaForm$Name"))))
                        && inst.isModifiableClass(clazz)) {
                    inst.retransformClasses(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: remove this debug code
    private static void debugCode() throws IOException {
        Path dir = Paths.get(System.getProperty("user.dir"), "generated-classes");

        if (Files.exists(dir) && deleteDirectory(dir.toFile())) {
            Files.createDirectories(dir);
        } else if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        // End debug code
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}