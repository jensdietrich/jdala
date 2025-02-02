package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
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

        assertThrows(IllegalStateException.class, () -> obj.value = "bar");
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
        assertThrows(IllegalStateException.class, () -> obj2.value = "bar");
    }

    public void changeImmutableBox(Box box) {
        // fails as object is immutable
        assertThrows(IllegalStateException.class, () -> box.value = "bar");
    }

    @Test
    public void testImmutableOtherThread() {
        @Immutable Box obj = new Box("foo"); // foo must remain local

        assertInstanceOf(IllegalStateException.class,
                runInOtherThread(() -> {
                    obj.value = "bar";
                }));
    }
}