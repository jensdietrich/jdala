package nz.ac.wgtn.ecs.jdala.realWorldExamples;

import nz.ac.wgtn.ecs.jdala.StaticAgentTests;
import nz.ac.wgtn.ecs.jdala.annotation.Isolated;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.ThreadWithExceptionCapture;
import util.UtilMethods;

import static org.junit.jupiter.api.Assertions.*;

public class DeadLockTest extends StaticAgentTests {
    @Test
    public void deadLockTestWithoutJDala()
    {
        Object lock1 = new Object();
        Object lock2 = new Object();

        var deadlock = new Deadlock(lock1, lock2);

        var t1 = new ThreadWithExceptionCapture(() -> {
            deadlock.method1(); // Executes for at least 1000ms
        });
        t1.start();

        var t2 = new ThreadWithExceptionCapture(() -> {
            deadlock.method2(); // Executes for at least 1000ms
        });
        t2.start();

        UtilMethods.tryToSleep(5000); // We need to wait for 2s + 2s + some more to be sure...

        assertEquals(Thread.State.BLOCKED, t1.getState());
        assertTrue(t1.isAlive());

        assertEquals(Thread.State.BLOCKED, t2.getState());
        assertTrue(t2.isAlive());
    }

    @Disabled("Test only uses synchronized to cause a deadlock, no access to isolated variables")
    @Test
    public void deadLockTestWithJDala()
    {
        @Isolated Object lock1 = new Object();
        @Isolated Object lock2 = new Object();

        var deadlock = new Deadlock(lock1, lock2);

        var t1 = new ThreadWithExceptionCapture(() -> {
            deadlock.method1(); // Executes for at least 1000ms
        });
        t1.start();

        var t2 = new ThreadWithExceptionCapture(() -> {
            deadlock.method2(); // Executes for at least 1000ms
        });
        t2.start();

        UtilMethods.tryToSleep(5000); // We need to wait for 2s + 2s + some more to be sure...

        assertEquals(Thread.State.TERMINATED, t1.getState());
        assertEquals(DalaCapabilityViolationException.class, t1.getException().getClass());

        assertEquals(Thread.State.TERMINATED, t2.getState());
        assertEquals(DalaCapabilityViolationException.class, t2.getException().getClass());
    }

    class Deadlock {
        final private Object monitor1;
        final private Object monitor2;

        public Deadlock(Object monitor1, Object monitor2) {
            this.monitor1 = monitor1;
            this.monitor2 = monitor2;
        }

        public void method1() {
            synchronized (monitor1) {
                UtilMethods.tryToSleep(1000);
                synchronized (monitor2) {
                    UtilMethods.tryToSleep(1000);
                }
            }
        }

        public void method2() {
            synchronized (monitor2) {
                UtilMethods.tryToSleep(1000);
                synchronized (monitor1) {
                    UtilMethods.tryToSleep(1000);
                }
            }
        }
    }

}