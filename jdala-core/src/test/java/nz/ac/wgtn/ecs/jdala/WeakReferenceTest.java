package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import org.junit.jupiter.api.Test;
import util.Box;

import java.lang.ref.Cleaner;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class WeakReferenceTest extends StaticAgentTests {

    private static final Cleaner cleaner = Cleaner.create();

    @Test
    public void testImmutableReference1 () throws InterruptedException {
        assumeTrue(JDala.immutableObjectsList.isEmpty());
        createImmutableBox();

        System.out.println("Prompting GC");
        System.gc();

        System.out.println("Waiting for GC ");

        sleep(1000);
//        System.out.println(JDala.immutableObjectsList + " " + JDala.immutableObjectsList.isEmpty());
        assertTrue(JDala.immutableObjectsList.isEmpty());
    }

    private void createImmutableBox() {
        @Immutable Box obj = new Box("foo");
        System.out.println("Creating immutable box: " + obj);

        cleaner.register(obj, () -> System.out.println("Box is being garbage collected! " + JDala.immutableObjectsList.isEmpty()));

        assertEquals(1, JDala.immutableObjectsList.size());
    }
}
