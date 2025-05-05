package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Local;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static util.ThreadRunner.runInOtherThread;

/**
 * Test the {@link Local} annotation works as expected
 *
 * @author Quinten Smit
 */
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
    public void testLocalArray1() {
        @Local Object[] boxArray = new Object[10];

        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // Add local object to Local array
        boxArray[0] = obj;

        // Should be fine to read/ remove objects from array in same thread
        Box objBox = (Box)boxArray[0];

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    @Local Box obj2 = new Box("bar");
                    // Fails: Should not be ok to write to a local array in another thread
                    boxArray[1] = obj2;
                }));
    }

    /**
     * Check that any time an object is read from an array the array is also validated
     */
    @Test
    public void testLocalArray2() throws InterruptedException {
        @Local Object[] boxArray = new Object[10];

        @Local Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // Add local object to Local array
        boxArray[0] = obj;

        // Should be fine to read/ remove objects from array in same thread
        Box objBox = (Box)boxArray[0];

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    // Fails: Should not be ok to read from local array in another thread
                    Box b = (Box)boxArray[0];
                }));
    }

    /**
     * Check that @Local can't have null registered to it
     */
    @Test
    public void testLocalNull1() {
        @Local Box obj = null;

        runInOtherThread(() -> {
            Box b = null;
        });
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
                    a.value = "Local_Violating_String";
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
                    Box b = new Box(obj);
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