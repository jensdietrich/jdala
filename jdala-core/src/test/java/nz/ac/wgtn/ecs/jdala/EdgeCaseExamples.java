package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EdgeCaseExamples extends StaticAgentTests{

    /**
     * This test is meant to show the effects of an exception type that the Java Runtime Environment doesn't expect to
     * see in an internal library so it reacts to it with a fatal error
     * @throws InterruptedException
     */
    @Disabled("Can't recreate it after changes made but I think it might still be there")
    @Test
    public void javaInternalException() throws InterruptedException {
        @Immutable StringBuilder sb = new StringBuilder("Hello");
        assertThrows(DalaCapabilityViolationException.class, () -> sb.append(" World!")); // This should trigger an error if your annotation processor works.
    }

}
