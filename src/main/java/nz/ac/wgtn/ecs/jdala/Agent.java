package nz.ac.wgtn.ecs.jdala;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new JdalaTransformer(), true);
    }
}