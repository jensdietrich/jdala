package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocalTest extends DynamicAgentTests{

    @Test
    public void testLocal1() {
        new LocalTest1().testLocal1();
    }

    @Test
    public void testLocal2() {
        new LocalTest2().testLocal2();
    }

    @Test
    public void testLocal3() {
        new LocalTest3().testLocal3();
    }

    @Test
    public void testLocal4() {
        new LocalTest4().testLocal4();
    }
}

class LocalTest1 {
    public void testLocal1() {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds
        obj.value = "bar";
    }
}

class LocalTest2 {
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
}

class LocalTest3 {
    BlockingQueue<Box> queue = new ArrayBlockingQueue<>(10);

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

class LocalTest4 {
//    BlockingQueue<Box> queue = new ArrayBlockingQueue<>(10);

    public void testLocal4() {
        Box a = new Box("food"); // food is unsafe
        @Local Box obj = new Box("foo"); // foo must remain local

        Box aliasObj = obj; // foo is still local so

        obj = new Box("bar");

//        assertThrows(IllegalStateException.class, () -> queue.put(aliasObj));
    }
}