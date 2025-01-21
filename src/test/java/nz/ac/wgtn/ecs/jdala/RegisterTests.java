package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Isolated;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests extends StaticAgentTests{
    @Test
    public void testRegisterLocal1() {
        @Local Box obj = new Box("foo");

        assertTrue(JDala.localThreadMap.containsKey(obj), "Thread Map should contain reference to " + obj + " but only contained " + JDala.localThreadMap);
        assertEquals(Thread.currentThread(), JDala.localThreadMap.get(obj));
//        assertEquals(1, JDala.localThreadMap.size());
    }

    @Test
    public void testRegisterLocal2() {
        @Local Box obj2 = new Box("foo");
        Box unsafe = new Box("bar");

        assertTrue(JDala.localThreadMap.containsKey(obj2), "Thread Map should contain reference to " + obj2 + " but contained " + JDala.localThreadMap);
        assertEquals(Thread.currentThread(), JDala.localThreadMap.get(obj2));
//        assertEquals(1, JDala.localThreadMap.size());
        assertFalse(JDala.localThreadMap.containsKey(unsafe));
    }

    @Test
    public void testRegisterImmutable1() {
        @Immutable Box obj = new Box("foo");

        assertTrue(JDala.immutableObjectsList.contains(obj));
//        assertEquals(1, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterImmutable2() {
        @Immutable Box obj = new Box("foo");
        Box unsafe = new Box("bar");

        assertTrue(JDala.immutableObjectsList.contains(obj));
        assertFalse(JDala.immutableObjectsList.contains(unsafe));
//        assertEquals(1, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterUnsafe1() {
        Box unsafe = new Box("bar");

        assertFalse(JDala.localThreadMap.containsKey(unsafe));
//        assertEquals(0, JDala.localThreadMap.size());
        assertFalse(JDala.immutableObjectsList.contains(unsafe));
//        assertEquals(0, JDala.immutableObjectsList.size());
    }

    @Test
    public void testRegisterDouble1(){
        @Local Double num = 15.0;

        assertTrue(JDala.localThreadMap.containsKey(num));
    }

    @Test
    public void testRegisterArray1(){
        Box volvo = new Box("Volvo");
        Box bmw = new Box("BMW");
        Box ford = new Box("Ford");
        Box mazda = new Box("Mazda");
        @Immutable Box[] cars = {volvo, bmw, ford, mazda};

        assertTrue(JDala.immutableObjectsList.contains(cars), "Immutable should contain reference to " + cars + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(volvo), "Immutable should contain reference to " + volvo + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(bmw), "Immutable should contain reference to " + bmw + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(ford), "Immutable should contain reference to " + ford + " but contained " + JDala.immutableObjectsList);
        assertTrue(JDala.immutableObjectsList.contains(mazda), "Immutable should contain reference to " + mazda + " but contained " + JDala.immutableObjectsList);
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
    }

//    @Test
//    public void testRegisterArray1(){
//        Box volvo = new Box("Volvo");
//        Box bmw = new Box("BMW");
//        Box ford = new Box("Ford");
//        Box mazda = new Box("Mazda");
//        @Immutable Box[] cars = {volvo, bmw, ford, mazda};
//
//        assertTrue(JDala.immutableObjectsList.contains(volvo), "Immutable should contain reference to " + volvo + " but contained " + JDala.immutableObjectsList);
//        assertTrue(JDala.immutableObjectsList.contains(bmw), "Immutable should contain reference to " + bmw + " but contained " + JDala.immutableObjectsList);
//        assertTrue(JDala.immutableObjectsList.contains(ford), "Immutable should contain reference to " + ford + " but contained " + JDala.immutableObjectsList);
//        assertTrue(JDala.immutableObjectsList.contains(mazda), "Immutable should contain reference to " + mazda + " but contained " + JDala.immutableObjectsList);
//    }

    @Disabled("Not ready yet") @Test
    public void testRegisterIsolated1() {
        @Isolated Box obj = new Box("foo");
//        assertTrue(JDa.contains(obj));
    }
}
