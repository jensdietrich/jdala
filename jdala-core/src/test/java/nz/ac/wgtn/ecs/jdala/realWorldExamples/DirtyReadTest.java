package nz.ac.wgtn.ecs.jdala.realWorldExamples;

import nz.ac.wgtn.ecs.jdala.StaticAgentTests;
import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Isolated;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Test;
import util.ThreadWithExceptionCapture;
import util.UtilMethods;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

public class DirtyReadTest extends StaticAgentTests {

    /**
     * This examples has sleeps in it to force order of actions that might cause an error.
     *
     * This test has no assertions, it can not fail it is only to show an example of what might happen
     */
    @Test
    public void dirtyReadTestWithoutJDala() {
        StorageObject example = new StorageObject();

        Thread writer = new Thread(() -> example.writeMessage("Updated"));
        Thread reader = new Thread(() -> {
            UtilMethods.tryToSleep(500);
            example.readMessage();
        });

        writer.start();
        reader.start();

        UtilMethods.tryToSleep(2000);
    }

    @Test
    public void dirtyReadTestWithJDalaImmutable() {
        @Immutable StorageObject example = new StorageObject();

        ThreadWithExceptionCapture writer = new ThreadWithExceptionCapture(() -> example.writeMessage("Updated"));
        ThreadWithExceptionCapture reader = new ThreadWithExceptionCapture(() -> {
            UtilMethods.tryToSleep(500);
            example.readMessage();
        });

        writer.start();
        reader.start();

        UtilMethods.tryToSleep(2000);

        assertEquals(Thread.State.TERMINATED, writer.getState());
        assertEquals(DalaCapabilityViolationException.class, writer.getException().getClass()); // Thread ends with exception because no write is allowed

        assertEquals(Thread.State.TERMINATED, reader.getState());
        assertNull(reader.getException());

        assertEquals("Initial", example.getMessage()); // Message isn't changed
    }

    @Test
    public void dirtyReadTestWithJDalaIsolated() throws InterruptedException {

        BlockingQueue<StorageObject> queue = new ArrayBlockingQueue<>(10);

        @Isolated StorageObject example = new StorageObject();

        queue.put(example);

        ThreadWithExceptionCapture writer = new ThreadWithExceptionCapture(() -> {
            synchronized (queue) {
                StorageObject storageObject = getObjectOrWait(queue);
                storageObject.writeMessage("Updated");
                queue.add(storageObject);
            }
        });

        ThreadWithExceptionCapture reader = new ThreadWithExceptionCapture(() -> {
            UtilMethods.tryToSleep(500);
            synchronized (queue) {
                StorageObject storageObject = getObjectOrWait(queue);
                storageObject.readMessage();
                queue.add(storageObject);
            }
        });

        writer.start();
        reader.start();

        UtilMethods.tryToSleep(2000);

        StorageObject storageObject = getObjectOrWait(queue);

        assertEquals(Thread.State.TERMINATED, writer.getState());
        assertNull(reader.getException());

        assertEquals(Thread.State.TERMINATED, reader.getState());
        assertNull(reader.getException());

        assertEquals("Updated", storageObject.getMessage()); // Message is changed
        assertTrue(storageObject.isCommitted());
    }

    private StorageObject getObjectOrWait(Queue<StorageObject> queue){
//        while (queue.isEmpty()) {
//            StorageObject.tryToSleep(5);
//        }
        return queue.poll();
    }


    static class StorageObject {
        private String message = "Initial";
        private boolean committed = false;

        public void writeMessage(String newMessage) {
            message = newMessage;
            UtilMethods.tryToSleep(1000);
            committed = true;
        }

        public void readMessage() {
            if (!committed) {
                System.out.println("Dirty Read: " + message);
            } else {
                System.out.println("Clean Read: " + message);
            }
        }

        public String getMessage() {
            return message;
        }

        public boolean isCommitted() {
            return committed;
        }
    }
}
