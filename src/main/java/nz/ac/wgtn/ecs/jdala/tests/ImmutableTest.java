package nz.ac.wgtn.ecs.jdala.tests;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import org.junit.jupiter.api.Test;
import util.Box;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableTest implements DynamicAgentTests{

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

    public void changeImmutableBox(Box box) {
        // fails as object is immutable
        assertThrows(IllegalStateException.class, () -> box.value = "bar");
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

}
