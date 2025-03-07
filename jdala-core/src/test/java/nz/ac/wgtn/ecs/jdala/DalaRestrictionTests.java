package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Isolated;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaRestrictionException;
import org.junit.jupiter.api.Test;
import util.Box;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test that no illegal actions can be taken.
 * e.g. changing an Immutable to a Local
 *
 * @author Quinten Smit
 */
public class DalaRestrictionTests extends StaticAgentTests {

    /////////////////////////// Immutable ///////////////////////////////

    @Test
    public void testChangeImmutableLocal1() {
        @Immutable Box obj = new Box("foo");

        assertThrows(DalaRestrictionException.class,
                () -> {@Local Box obj2 = obj;});
    }

    /////////////////////////// Isolated ///////////////////////////////

    @Test
    public void testIsolatedUnsafe1() {
        @Isolated Box obj = new Box("foo");

        assertThrows(DalaRestrictionException.class,
                () -> {obj.value = new Object();});
    }

    @Test
    public void testIsolatedLocal1() {
        @Isolated Box obj = new Box("foo");

        assertThrows(DalaRestrictionException.class,
                () -> {@Local Box obj2 = new Box("bar");
                        obj.value = obj2;});
    }

    @Test
    public void testIsolatedIsolated1() {
        @Isolated Box obj = new Box("foo");

        @Isolated Box obj2 = new Box("bar");
        obj.value = obj2;
    }

    @Test
    public void testIsolatedImmutable1() {
        @Isolated Box obj = new Box("foo");

        @Immutable Box obj2 = new Box("bar");
        obj.value = obj2;
    }

    /////////////////////////// Local ///////////////////////////////

    @Test
    public void testChangeLocalImmutable1() {
        @Local Box obj = new Box("foo");

        @Immutable Box obj2 = obj;
    }

    @Test
    public void testLocalUnsafe1() {
        @Local Box obj = new Box("foo");

        assertThrows(DalaRestrictionException.class,
                () -> {obj.value = new Object();});
    }

    @Test
    public void testLocalLocal1() {
        @Local Box obj = new Box("foo");

        @Local Box obj2 = new Box("bar");
        obj.value = obj2;
    }

    @Test
    public void testLocalIsolated1() {
        @Local Box obj = new Box("foo");

        @Isolated Box obj2 = new Box("bar");
        obj.value = obj2;
    }

    @Test
    public void testLocalImmutable1() {
        @Local Box obj = new Box("foo");

        @Immutable Box obj2 = new Box("bar");
        obj.value = obj2;
    }
}
