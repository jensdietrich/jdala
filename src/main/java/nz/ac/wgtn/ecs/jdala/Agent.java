package nz.ac.wgtn.ecs.jdala;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;

public class Agent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        // TODO: remove this debug code
        File folder = new File(System.getProperty("user.dir"));
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith("-t.class")) {
                f.delete();
            }
        }
        // End debug code

        inst.addTransformer(new JdalaTransformer(), true);
    }

    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException {
        // TODO: remove this debug code
        File folder = new File(System.getProperty("user.dir"));
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith("-t.class")) {
                f.delete();
            }
        }
        // End debug code
//        System.load(Path.of("target/classes/nz/ac/wgtn/ecs/jdala/ThreadChecker.class").toAbsolutePath().toString());
//        System.out.println(Class.forName("nz.ac.wgtn.ecs.jdala.ThreadChecker"));

        inst.addTransformer(new JdalaTransformer(), true);
    }
}