package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Isolated;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static util.ThreadRunner.runInOtherThread;

/**
 * Test that any object annotated is correctly registered in the JDala class.
 *
 * @author Quinten Smit
 */
public class RegisterTests extends StaticAgentTests{
    public final int ARRAYLIST_INTERNAL_VARIABLE_COUNT = 6;

    @Test
    public void testRegisterLocal1() {
        @Local Box obj = new Box("foo");

        assertTrue(JDala.localThreadMap.containsKey(obj), "Thread Map should contain reference to " + obj + " but only contained " + JDala.localThreadMap);
        assertEquals(Thread.currentThread(), JDala.localThreadMap.get(obj));
        assertEquals(2, JDala.localThreadMap.size());
    }

    @Test
    public void testRegisterLocal2() {
        @Local Box obj2 = new Box("foo");
        Box unsafe = new Box("bar");

        assertTrue(JDala.localThreadMap.containsKey(obj2), "Thread Map should contain reference to " + obj2 + " but contained " + JDala.localThreadMap);
        assertEquals(Thread.currentThread(), JDala.localThreadMap.get(obj2));
        assertEquals(2, JDala.localThreadMap.size());
        assertFalse(JDala.localThreadMap.containsKey(unsafe));
    }

    @Test
    public void testRegisterLocal3() {
        Box box = new Box("bar");

        Thread mainThread = Thread.currentThread();
        AtomicReference<Thread> otherThread = new AtomicReference<>();
        runInOtherThread(() -> {
            @Local Box b = box;
            otherThread.set(Thread.currentThread());
        });

        assumeFalse(mainThread.equals(otherThread.get()));

        assertTrue(JDala.localThreadMap.containsKey(box), "Thread Map should contain reference to " + box + " but contained " + JDala.localThreadMap);
        assertEquals(otherThread.get(), JDala.localThreadMap.get(box));
        assertEquals(2, JDala.localThreadMap.size());
    }

    @Test
    public void testRegisterImmutable1() {
        @Immutable Box obj = new Box("foo");

        assertTrue(JDala.immutableObjectsList.contains(obj));
        // String "foo" isn't stored as it is a primitive and should be immutable regardless
        assertEquals(1, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterImmutable2() {
        @Immutable Box obj = new Box("foo");
        Box unsafe = new Box("bar");

        assertTrue(JDala.immutableObjectsList.contains(obj));
        assertFalse(JDala.immutableObjectsList.contains(unsafe));
        // String "foo" isn't stored as it is a primitive and should be immutable regardless
        assertEquals(1, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterUnsafe1() {
        Box unsafe = new Box("bar");

        assertFalse(JDala.localThreadMap.containsKey(unsafe));
        assertEquals(0, JDala.localThreadMap.size());
        assertFalse(JDala.immutableObjectsList.contains(unsafe));
        assertEquals(0, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterDouble1(){
        @Local Double num = 15.0;

        assertTrue(JDala.localThreadMap.containsKey(num));
        assertEquals(1, JDala.localThreadMap.size());
    }

    @Test
    public void testRegisterArray1(){
        Box volvo = new Box("Volvo");
        Box bmw = new Box("BMW");
        Box ford = new Box("Ford");
        Box mazda = new Box("Mazda");
        @Immutable Box[] cars = {volvo, bmw, ford, mazda};

        assertTrue(JDala.immutableObjectsList.contains(cars), "Immutable should contain reference to " + Arrays.toString(cars) + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(volvo), "Immutable should contain reference to " + volvo + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(bmw), "Immutable should contain reference to " + bmw + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(ford), "Immutable should contain reference to " + ford + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(mazda), "Immutable should contain reference to " + mazda + " but contained " + JDala.immutableObjectsList);
        assertEquals(9, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterArrayList1(){
        Box volvo = new Box("Volvo");
        Box bmw = new Box("BMW");
        Box ford = new Box("Ford");
        Box mazda = new Box("Mazda");
        ArrayList<Box> cars = new ArrayList<>();

        cars.add(volvo);
        cars.add(bmw);
        cars.add(ford);
        cars.add(mazda);

        @Immutable ArrayList<Box> cars2 = cars;

//        assumeEquals(cars, cars2);

        assertTrue(JDala.immutableObjectsList.contains(cars), "Immutable should contain reference to " + cars + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(volvo), "Immutable should contain reference to " + volvo + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(bmw), "Immutable should contain reference to " + bmw + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(ford), "Immutable should contain reference to " + ford + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(mazda), "Immutable should contain reference to " + mazda + " but contained " + JDala.immutableObjectsList);
        assertEquals(9 + ARRAYLIST_INTERNAL_VARIABLE_COUNT, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterDeep1(){
        Box foo = new Box("Foo");
        Box bar = new Box(foo);
        Box[] foobars = {bar};
        ArrayList<Box[]> foobarsArrayList = new ArrayList<>();
        foobarsArrayList.add(foobars);
        @Immutable ArrayList<Box[]> immutable = foobarsArrayList;

        assertTrue(JDala.immutableObjectsList.contains(immutable), "Immutable should contain reference to " + immutable + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(foobarsArrayList), "Immutable should contain reference to " + foobarsArrayList + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(foobars), "Immutable should contain reference to " + Arrays.toString(foobars) + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(bar), "Immutable should contain reference to " + bar + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(foo), "Immutable should contain reference to " + foo + " but contained " + JDala.immutableObjectsList);
        assertEquals(5 + ARRAYLIST_INTERNAL_VARIABLE_COUNT, JDala.immutableObjectsList.size());
    }

    @Disabled("Not ready yet") @Test
    public void testRegisterIsolated1() {
        @Isolated Box obj = new Box("foo");
//        assertTrue(JDa.contains(obj));
    }
}
