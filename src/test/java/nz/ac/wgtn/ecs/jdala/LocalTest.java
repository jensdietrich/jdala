package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Local;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocalTest extends DynamicAgentTests{

    @Test
    public void testLocal1() {
        new LocalTests().testLocal1();
    }

    @Test
    public void testLocal2() {
        new LocalTests().testLocal2();
    }

    @Test
    public void testLocal3() {
        new LocalTests().testLocal3();
    }
}

class LocalTests{
    public void testLocal1() {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds
        obj.value = "bar";
    }

    public void testLocal2() {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds , thread-local alias
        m(obj);
    }

    public static void m(Box box) {
        // succeeds
        box.value = "bar";
    }

    static BlockingQueue<Box> queue = new ArrayBlockingQueue<>(10);

    public void testLocal3() {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds
        obj.value = "bar";

        // fails -- queue is a *transfer object* to pass object to another thread
        // NOTE: it is perhaps better to enforce this on the consumer side, ie when another
        // thread calls queue::take
        // is there a good abstraction for such transfer objects ?
        assertThrows(IllegalStateException.class, () -> queue.put(obj));
    }
}