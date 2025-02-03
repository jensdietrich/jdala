package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Local;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaRestrictionException;
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

        Object d = obj.value;

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    try {
                        Box b = queue.take();
                        Object o = b.value;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    @Test
    public void testOtherThreadEdit1() {
        Box a = new Box("food"); // food is unsafe
        @Local Box obj = new Box("foo"); // foo must remain local

        Box aliasObj = obj; // foo is still local so this is accepted

        obj = new Box("bar");

        obj.value = "bar2";

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    aliasObj.value = "Local_Violating_String";
                }));
    }

    @Test
    public void testOtherThreadRead1() {
        @Local Box localBox = new Box("foo"); // foo must remain local

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    Object obj = localBox.value;
                }));
    }

    @Test
    public void testOtherThreadAlias1() throws IllegalAccessException {
        @Local Box a = new Box(new Box("box"));
//        a.value = obj;

//        ArrayList<Box> list = new ArrayList<>();
//        list.add(a);

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    Box b = a;
                    b.value = "Local_Violating_String";
                }));
    }

    @Test
    public void testDeepLocal1() throws IllegalAccessException {
        Box deepObj = new Box("foo");

        @Local Box obj = new Box(deepObj);

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    // Should not be able to access sub-objects of a @Local object
                    String b = (String)deepObj.value;
                    b.contains("o");
                }));
    }

    @Test
    public void testConstructorLocal1() throws IllegalAccessException {
        @Local Box obj = new Box("foo");

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    Box otherBox = new Box(obj);
                    Box b = otherBox;
                }));
    }

    @Test
    public void testConstructorLocal2(){
        @Local Box obj = new Box("foo");

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    C2 c2 = new C2(obj);
                }));
    }

    private class C2{
        public Box box;
        public C2(Box box){
            this.box = box;
        }

    }
}