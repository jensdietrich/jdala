package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Local;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static util.ThreadRunner.runInOtherThread;

public class LocalTest extends StaticAgentTests {

    @Test
    public void testLocal1() {
        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds
        obj.value = "bar";
    }

    @Test
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

    @Test
    public void testLocal3() throws InterruptedException {
        BlockingQueue<Box> queue = new ArrayBlockingQueue<>(10);

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

    @Test
    public void testLocal4() throws InterruptedException {
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

    @Test
    public void testLocal5() throws IllegalAccessException {
        Box a = new Box(new Box("box"));
//        a.value = obj;

        ArrayList<Box> list = new ArrayList<>();
        list.add(a);


//        System.out.println(ThreadChecker.retrieveAllSubObjects(a));

        System.out.println(JDala.retrieveAllSubObjects(list));

        assertInstanceOf(IllegalStateException.class,
                runInOtherThread(() -> {
                    Box b = a;
                }));
    }

    @Test
    public void testDeepLocal1() throws IllegalAccessException {
        Box deepObj = new Box("foo");

        @Local Box obj = new Box(deepObj);

        assertInstanceOf(IllegalStateException.class,
                runInOtherThread(() -> {
                    // Should not be able to access sub-objects of a @Local object
                    Box b = deepObj;
                }));
    }
}