package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static util.ThreadRunner.runInOtherThread;

/**
 * Test the {@link Immutable} annotation works as expected
 *
 * @author Quinten Smit
 */
public class ImmutableTest extends StaticAgentTests {

    @Test
    public void testImmutable1 () {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        assertThrows(DalaCapabilityViolationException.class, () -> obj.value = "bar");
    }

    @Test
    public void testImmutable2() {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // succeeds - objects can be aliased
        changeImmutableBox(obj);
    }

    @Test
    public void testImmutable3() {
        @Immutable Box obj = new Box("foo");
        // now the object pointed to by obj is annotated (not the var)

        // local alias succeeds
        Box obj2 = obj;

        // fails as object obj2 points to is immutable
        assertThrows(DalaCapabilityViolationException.class, () -> obj2.value = "bar");
    }

    public void changeImmutableBox(Box box) {
        // fails as object is immutable
        assertThrows(DalaCapabilityViolationException.class, () -> box.value = "bar");
    }

    /**
     * Check that @Immutable can't have null registered in it
     */
    @Test
    public void testImmutableNull1() {
        @Immutable Box obj = null;

        runInOtherThread(() -> {
            Box b = null;
        });
    }

    @Test
    public void testImmutableOtherThread() {
        @Immutable Box obj = new Box("foo"); // foo must remain local

        assertInstanceOf(DalaCapabilityViolationException.class,
                runInOtherThread(() -> {
                    obj.value = "bar";
                }));
    }

    /**
     * Immutable objects can be read concurrently without risk.
     * Here, multiple threads read the same immutable Box safely.
     */
    @Test
    public void testImmutableConcurrentRead() throws InterruptedException {
        @Immutable Box box = new Box("constant value");
        final int numThreads = 100;

        BlockingQueue<Runnable> boxQueue = new ArrayBlockingQueue<>(numThreads);

        final Object[] results = new String[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            boxQueue.add(()-> results[index] = box.getValue());
        }

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 10, 10, TimeUnit.SECONDS, boxQueue);
        threadPoolExecutor.prestartAllCoreThreads();

        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);

        for (int i = 0; i < numThreads; i++) {
            assertEquals("constant value", results[i]);
        }
    }
}