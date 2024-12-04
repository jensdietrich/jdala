package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import org.junit.jupiter.api.Test;
import util.Box;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableTest extends DynamicAgentTests{

    @Test
    public void testImmutable1 () {
        new ImmutableTests().testImmutable1();
    }

    @Test
    public void testImmutable2() {
        new ImmutableTests().testImmutable2();
    }

    @Test
    public void testImmutable3() {
        new ImmutableTests().testImmutable3();
    }
}

class ImmutableTests{
    public void testImmutable1 () {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        assertThrows(IllegalStateException.class, () -> obj.value = "bar");
    }

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

    public void testImmutable3() {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // local alias succeeds
        Box obj2 = obj;

        // fails as object obj2 points to is immutable
        assertThrows(IllegalStateException.class, () -> obj2.value = "bar");
    }
}