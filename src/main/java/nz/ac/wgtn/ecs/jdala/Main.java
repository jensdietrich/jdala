package nz.ac.wgtn.ecs.jdala;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import nz.ac.wgtn.ecs.jdala.tests.LocalTest;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class Main {
    public static void main(String[] args) {
        try {
            attachAgentDynamically();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalTest.testLocal1();
        LocalTest.testLocal2();
        LocalTest.testLocal3();
    }

    private static void attachAgentDynamically() throws Exception {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        final long pid = runtime.getPid();
        VirtualMachine self = null;

        try {
            self = VirtualMachine.attach(""+pid);
        } catch (AttachNotSupportedException e) {
            System.out.println("Cannot attach to this VM, program must be started with JVM option \"-Djdk.attach.allowAttachSelf=true\"");
        }

        File agentJar = new File("target/jdala-agent.jar");
        if (!agentJar.exists()) {
            throw new IllegalStateException("agent jar not found -- build project with \"mvn package\" first");
        }

        try {
            self.loadAgent(agentJar.getAbsolutePath());
        } catch (AgentLoadException | AgentInitializationException x) {
            System.err.println("error loading agent");
            x.printStackTrace();
        }
    }
}
