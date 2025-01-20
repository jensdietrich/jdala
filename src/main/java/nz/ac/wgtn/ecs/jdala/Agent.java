package nz.ac.wgtn.ecs.jdala;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, IOException {
        debugCode();
        
        File file = new File("target/jdala-agent.jar");
        if (!file.exists()) {
            throw new IllegalStateException("file not found: " + file.getAbsolutePath());
        }
        JarFile jarFile = new JarFile(file);
        inst.appendToBootstrapClassLoaderSearch(jarFile);
        System.out.println("Starting agent");
        inst.addTransformer(new JDalaTransformer(), true);
    }

    private static void debugCode() throws IOException {
        // TODO: remove this debug code
        File folder = new File(System.getProperty("user.dir"));
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith(".class")) {
                f.delete();
            }
        }
        Path dir = Paths.get("generated-classed");
        deleteDirectory(dir.toFile());
        Files.createDirectories(dir);
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