package nz.ac.wgtn.ecs.jdala;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test that the immutable classes resource file correctly recognizes and enforces the constraints imposed by the resource file.
 *
 * @author Quinten Smit
 */
public class ResourcesFileTest {

    @Test
    public void testString() {
        Object string = "Hello World";
        assertTrue(JDala.isImmutable(string));
    }

    @Test
    public void testInteger() {
        Object integer = Integer.valueOf(3);
        assertTrue(JDala.isImmutable(integer));
    }

    @Test
    public void testLong() {
        Object longBox = Long.valueOf(3);
        assertTrue(JDala.isImmutable(longBox));
    }

    @Test
    public void testBoolean() {
        Object localDate = java.time.LocalDate.now();
        assertTrue(JDala.isImmutable(localDate));
    }

    @Test
    public void testBigDecimal() {
        Object bigDecimal = BigDecimal.valueOf(3);
        assertTrue(JDala.isImmutable(bigDecimal));
    }

    @Test
    public void testObjectFails() {
        Object obj = new Object();
        assertFalse(JDala.isImmutable(obj));
    }
}
