package nz.ac.wgtn.ecs.jdala;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

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
        Object booleanBox = Boolean.valueOf(false);
        assertTrue(JDala.isImmutable(booleanBox));
    }

    @Test
    public void testDouble() {
        Object doubleWrapper = Double.valueOf(3.14);
        assertTrue(JDala.isImmutable(doubleWrapper));
    }

    @Test
    public void testFloat() {
        Object floatWrapper = Float.valueOf(363);
        assertTrue(JDala.isImmutable(floatWrapper));
    }

    @Test
    public void testByte() {
        Object byteWrapper = Byte.valueOf((byte) 4);
        assertTrue(JDala.isImmutable(byteWrapper));
    }

    @Test
    public void testCharacter() {
        Object characterWrapper = Character.valueOf('c');
        assertTrue(JDala.isImmutable(characterWrapper));
    }

    @Test
    public void testLocalDate() {
        Object localDate = java.time.LocalDate.now();
        assertTrue(JDala.isImmutable(localDate));
    }

    @Test
    public void testBigInteger() {
        Object bigInteger = BigInteger.valueOf(3);
        assertTrue(JDala.isImmutable(bigInteger));
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
