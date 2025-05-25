package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Test;
import util.Box;
import util.ThreadWithExceptionCapture;
import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;


public class ReflectionTests extends StaticAgentTests {

    @Test
    public void testReflectionLocalGetInOtherThead() throws InterruptedException {
        @Local Box obj = new Box("foo");

        ThreadWithExceptionCapture thread1 = new ThreadWithExceptionCapture(()->{
            Class<?> clazz = obj.getClass();
            Field field = null;
            try {
                field = clazz.getDeclaredField("value");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            Object containedObject = null;
            try {
                containedObject = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            System.out.println(containedObject);
        });

        thread1.start();
        thread1.join();

        assertNotNull(thread1.getException());
        assertEquals(DalaCapabilityViolationException.class, thread1.getException().getClass());
    }

    @Test
    public void testReflectionImmutableGetInOtherThead() throws InterruptedException {
        @Immutable Box obj = new Box("foo");

        ThreadWithExceptionCapture thread1 = new ThreadWithExceptionCapture(()->{
            Class<?> clazz = obj.getClass();
            Field field = null;
            try {
                field = clazz.getDeclaredField("value");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            Object containedObject = null;
            try {
                containedObject = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            System.out.println(containedObject);
        });

        thread1.start();
        thread1.join();

        assertNull(thread1.getException());
    }

    @Test
    public void testReflectionImmutableSetIntInOtherThead() throws InterruptedException {
        @Immutable NumberBox obj = new NumberBox(10);

        assertThrows(DalaCapabilityViolationException.class,() -> {
            Class<?> clazz = obj.getClass();
            Field field = null;
            try {
                field = clazz.getDeclaredField("value");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                field.set(obj, 100);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println(obj.getValue());
    }

    private class NumberBox {
        public NumberBox(int value) {
            this.value = value;
        }
        public int value = -1;
        public int getValue() {
            return value;
        }
    }

    @Test
    public void testReflectionLocalSetInOtherThead() throws InterruptedException {
        @Local Box obj = new Box("foo");

        ThreadWithExceptionCapture thread1 = new ThreadWithExceptionCapture(()->{
            Class<?> clazz = obj.getClass();
            Field field = null;
            try {
                field = clazz.getDeclaredField("value");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                field.set(obj, "bar");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        thread1.start();
        thread1.join();

        System.out.println(obj.getValue());

        assertNotNull(thread1.getException());
        assertEquals(DalaCapabilityViolationException.class, thread1.getException().getClass());
    }

    @Test
    public void testReflectionImmutableSetInOtherThead() throws InterruptedException {
        @Immutable Box obj = new Box("foo");

        ThreadWithExceptionCapture thread1 = new ThreadWithExceptionCapture(()->{
            Class<?> clazz = obj.getClass();
            Field field = null;
            try {
                field = clazz.getDeclaredField("value");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                field.set(obj, "bar");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        thread1.start();
        thread1.join();

        System.out.println(obj.getValue());

        assertNotNull(thread1.getException());
        assertEquals(DalaCapabilityViolationException.class, thread1.getException().getClass());
    }

}
