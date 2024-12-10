package nz.ac.wgtn.ecs.jdala;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public abstract class DynamicAgentTests {

    @BeforeAll
    static void attachAgent(){
        ThreadChecker.reset(); // Load before attaching, so it doesn't show up in debug.
        try {
            attachAgentDynamically();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void reset(){
        ThreadChecker.reset();
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
