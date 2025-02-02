package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import org.junit.jupiter.api.Test;
import util.Box;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterCollisionTests extends StaticAgentTests {
    @Test
    public void testLocalImmutableCollision1() {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds
        @Immutable Box obj2 = obj;
    }

    @Test
    public void testLocalImmutableCollision2() {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // fails
        assertThrows(RuntimeException.class,
                () -> {@Local Box obj2 = obj;});
    }
}
