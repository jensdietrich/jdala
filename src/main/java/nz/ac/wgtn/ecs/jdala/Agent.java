package nz.ac.wgtn.ecs.jdala;

import java.io.File;
import java.lang.instrument.Instrumentation;

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
}