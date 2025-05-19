package nz.ac.wgtn.ecs.jdala.realWorldExamples;

import nz.ac.wgtn.ecs.jdala.StaticAgentTests;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Test;
import util.Box;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class LocalDataRaceTest extends StaticAgentTests {

    /**
     * This test simulates a data race on an unsafe (non-annotated) Box.
     * Two threads concurrently modify the same Box.
     * In an unsound system, the final contents of the Box might be unpredictable.
     */
    @Test
    public void testDataRaceUnsafeBox() throws InterruptedException {
        // Create an unsafe (non-annotated) Box.
        Box unsafeBox = new Box("initial");

        // This test simulates a scenario where concurrent unsynchronized modifications
        // lead to a data race. While the race may not always cause an exception,
        // its effect is unpredictable behavior.
        int iterations = 100;
        CountDownLatch latch = new CountDownLatch(2);

        Runnable updateTask = () -> {
            for (int i = 0; i < iterations; i++) {
                // Two threads update the same field concurrently; this is inherently racy.
                unsafeBox.setValue(Thread.currentThread().getName() + "-" + i);
                try {
                    Thread.sleep(1); // Introduce a small delay to widen the race window.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            latch.countDown();
        };

        Thread thread1 = new Thread(updateTask, "Thread1");
        Thread thread2 = new Thread(updateTask, "Thread2");
        thread1.start();
        thread2.start();
        latch.await();

        Object finalValue = unsafeBox.getValue();
        System.out.println("Final value in unsafeBox: " + finalValue);

        // Notice: There is no guarantee on the exact value here because a data race makes
        // the outcome unpredictable. In a production race, this unpredictability can lead to
        // subtle bugs. For our test we just verify the value is non-null.
        assertNotNull(finalValue);
    }

    /**
     * This test demonstrates that when a Box is annotated with @Local,
     * only the thread that created (owns) the object may modify it.
     * Any access from another thread is caught immediately with a violation exception.
     */
    @Test
    public void testDataRacePreventionWithLocalBox() throws InterruptedException {
        // Marking the Box as @Local indicates that its safe usage is confined
        // to the thread on which it is created.
        @Local Box localBox = new Box("initial");

        // The owner thread (current thread) can freely update the localBox.
        localBox.setValue("owner-update");
        assertEquals("owner-update", localBox.getValue());

        // Now simulate a data race: spawn a secondary (non-owner) thread that attempts
        // to update the same @Local object.
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> exceptionRef = new AtomicReference<>();

        Thread nonOwnerThread = new Thread(() -> {
            try {
                // This update should be rejected immediately by the runtime's capability check.
                localBox.setValue("non-owner-update");
            } catch (Throwable t) {
                // Capture the DalaCapabilityViolationException.
                exceptionRef.set(t);
            } finally {
                latch.countDown();
            }
        }, "NonOwnerThread");

        nonOwnerThread.start();
        latch.await();

        Throwable thrown = exceptionRef.get();
        assertNotNull(thrown, "Access from a non-owner thread should be prevented when the object is @Local.");
        assertInstanceOf(DalaCapabilityViolationException.class, thrown, "The exception thrown should be of type DalaCapabilityViolationException.");

        // Confirm that the value remains unchanged because the illegal update was rejected.
        assertEquals("owner-update", localBox.getValue());
    }
}
