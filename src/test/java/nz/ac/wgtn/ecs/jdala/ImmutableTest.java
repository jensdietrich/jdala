package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Test;
import util.Box;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static util.ThreadRunner.runInOtherThread;

public class ImmutableTest extends StaticAgentTests {

    @Test
    public void testImmutable1 () {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        assertThrows(DalaCapabilityViolationException.class, () -> obj.value = "bar");
    }

    @Test
    public void testImmutable2() {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds - objects can be aliased
        changeImmutableBox(obj);
    }

    @Test
    public void testImmutable3() {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // local alias succeeds
        Box obj2 = obj;

        // fails as object obj2 points to is immutable
        assertThrows(DalaCapabilityViolationException.class, () -> obj2.value = "bar");
    }

    public void changeImmutableBox(Box box) {
        // fails as object is immutable
        assertThrows(DalaCapabilityViolationException.class, () -> box.value = "bar");
    }

    /**
     * Check that @Immutable can't have null registered in it
     */
    @Test
    public void testImmutableNull1() {
        @Immutable Box obj = null;

        runInOtherThread(() -> {
            Box b = null;
        });
    }

    @Test
    public void testImmutableOtherThread() {
        @Immutable Box obj = new Box("foo"); // foo must remain local

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    obj.value = "bar";
                }));
    }
}