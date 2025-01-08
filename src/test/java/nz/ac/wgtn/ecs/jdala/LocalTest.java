package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Local;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static util.ThreadRunner.runInOtherThread;

public class LocalTest extends DynamicAgentTests{

    @Test
    public void testLocal1() {
        new LocalTest1().run();
    }

    @Test
    public void testLocal2() {
        new LocalTest2().run();
    }

    @Test
    public void testLocal3() throws InterruptedException {
        new LocalTest3().run();
    }

    @Test
    public void testLocal4() throws InterruptedException {
        new LocalTest4().run();
    }

    @Test
    public void testLocal5() throws IllegalAccessException {
        new LocalTest5().run();
    }

    @Test
    public void testDeepLocal1() throws IllegalAccessException {
        new LocalDeepTest1().run();
    }
}

class LocalTest1 {
    public void run() {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds
        obj.value = "bar";
    }
}

class LocalTest2 {
    public void run() {
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

    public void run() throws InterruptedException {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds
        obj.value = "bar";

        // fails -- queue is a *transfer object* to pass object to another thread
        // NOTE: it is perhaps better to enforce this on the consumer side, ie when another
        // thread calls queue::take
        // is there a good abstraction for such transfer objects ?
        queue.put(obj);

        assertInstanceOf(IllegalStateException.class,
                runInOtherThread(() -> {
                    try {
                        Box b = queue.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));

    }
}

class LocalTest4 {
    public void run() throws InterruptedException {
        Box a = new Box("food"); // food is unsafe
        @Local Box obj = new Box("foo"); // foo must remain local

        Box aliasObj = obj; // foo is still local so this is accepted

        obj = new Box("bar");

        obj.value = "bar2";

        assertInstanceOf(IllegalStateException.class,
                runInOtherThread(() -> {
//                    Box b = aliasObj;
                    aliasObj.value = "Local_Violating_String";
                }));
    }
}

class LocalTest5 {
    public void run() throws IllegalAccessException {
        Box a = new Box(new Box("box"));
//        a.value = obj;

        ArrayList<Box> list = new ArrayList<>();
        list.add(a);


//        System.out.println(ThreadChecker.retrieveAllSubObjects(a));

        System.out.println(ThreadChecker.retrieveAllSubObjects(list));

        assertInstanceOf(IllegalStateException.class,
                runInOtherThread(() -> {
                    Box b = a;
                }));
    }
}

class LocalDeepTest1 {
    public void run() {
        Box deepObj = new Box("foo");

        @Local Box obj = new Box(deepObj);

        assertInstanceOf(IllegalStateException.class,
                runInOtherThread(() -> {
                    // Should not be able to access sub-objects of a @Local object
                    Box b = deepObj;
                }));
    }
}