package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Isolated;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IsolatedTest extends DynamicAgentTests {
    @Test
    public void testIsolated1() {
        new IsolatedTests().testIsolated1();
    }

    @Test
    public void testIsolated2() {
        new IsolatedTests().testIsolated2();
    }

    @Test
    public void testIsolated3() throws InterruptedException {
        new IsolatedTests().testIsolated3();
    }

    @Test
    public void testIsolated4() {
        new IsolatedTests().testIsolated4();
    }
}

class IsolatedTests{
    public void testIsolated1() {
        @Isolated Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // success, no aliasing allowed but can tolerate in-method aliasing
        // as instrumentation resolved references
        Box m2 = obj;

        // success, object can be mutated
        m2.value = "bar";
    }

    public void testIsolated2() {
        @Isolated Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // fails - no aliasing allowed
        assertThrows(IllegalStateException.class, () -> m1(obj));
    }

    void m1(Box box) {}

    BlockingQueue<Box> queue = new ArrayBlockingQueue<>(10);

    public void testIsolated3() throws InterruptedException {
        @Isolated Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds, mutating is ok as long as the thread own the object
        obj.value = "bar";

        // succeeds, puts object in transfer state
        queue.put(obj);

        // fails, now control has been passed to another thread
        // so this thread cannot mutate this anymore
        assertThrows(IllegalStateException.class, () -> obj.value = "bar2");
    }

    public void testIsolated4() {
        @Isolated Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds - invocation-local aliasing can be tolerated
        // instrumentation will see actual object
        Box obj2 = obj;

        // fails - but in general aliasing is not permitted
        assertThrows(IllegalStateException.class, () -> m2(obj2));
    }

    public void m2(Box box) {
        // fails as object is immutable
        box.value = "bar";
    }
}
