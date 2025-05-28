package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.annotation.Local;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import org.junit.jupiter.api.Test;
import util.Box;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static util.ThreadRunner.runInOtherThread;

public class PrimitiveTests extends StaticAgentTests {

    @Test
    public void testSetBoolean() {
        @Immutable BooleanBox obj = new BooleanBox(true);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(false));
    }

    @Test
    public void testGetBoolean() {
        @Local BooleanBox obj = new BooleanBox(true);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class BooleanBox {
        boolean value;
        BooleanBox(boolean value) {setValue(value);}
        public void setValue(boolean value) {this.value=value;}
        public boolean getValue() {return value;}
    }

    @Test
    public void testSetIntoBooleanArray() {
        @Immutable boolean[] arr = new boolean[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = true);
    }

    @Test
    public void testGetIntoBooleanArray() {
        @Local boolean[] arr = new boolean[] { true, false, true, true};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{boolean i = arr[2];}));
    }

    @Test
    public void testSetChar() {
        @Immutable CharBox obj = new CharBox('c');
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue('f'));
    }

    @Test
    public void testGetChar() {
        @Local CharBox obj = new CharBox('c');
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class CharBox {
        char value;
        CharBox(char value) {setValue(value);}
        public void setValue(char value) {this.value=value;}
        public int getValue() {return value;}
    }

    @Test
    public void testSetIntoCharArray() {
        @Immutable char[] arr = new char[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = 1);
    }

    @Test
    public void testGetIntoCharArray() {
        @Local char[] arr = new char[] { 1, 2, 3, 4};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{char i = arr[2];}));
    }

    @Test
    public void testSetByte() {
        @Immutable ByteBox obj = new ByteBox((byte) 1);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue((byte)2));
    }

    @Test
    public void testGetByte() {
        @Local ByteBox obj = new ByteBox((byte) 1);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class ByteBox {
        byte value;
        ByteBox(byte value) {setValue(value);}
        public void setValue(byte value) {this.value=value;}
        public byte getValue() {return value;}
    }

    @Test
    public void testSetIntoByteArray() {
        @Immutable byte[] arr = new byte[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = 1);
    }

    @Test
    public void testGetIntoByteArray() {
        @Local byte[] arr = new byte[] { 1, 2, 3, 4};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{byte i = arr[2];}));
    }

    @Test
    public void testSetShort() {
        @Immutable ShortBox obj = new ShortBox((short) 1);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue((short) 4));
    }

    @Test
    public void testGetShort() {
        @Local ShortBox obj = new ShortBox((short) 1);
        runInOtherThread(()->obj.getValue());
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class ShortBox {
        short value;
        ShortBox(short value) {setValue(value);}
        public void setValue(short value) {this.value=value;}
        public short getValue() {return value;}
    }

    @Test
    public void testSetIntoShortArray() {
        @Immutable short[] arr = new short[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = 1);
    }

    @Test
    public void testGetIntoShorArray() {
        @Local short[] arr = new short[] { 1, 2, 3, 4};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{short i = arr[2];}));
    }

    @Test
    public void testSetInt() {
        @Immutable IntBox obj = new IntBox(-1);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(5));
    }

    @Test
    public void testGetInt() {
        @Local IntBox obj = new IntBox(-1);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class IntBox {
        int value;
        IntBox(int value) {setValue(value);}
        public void setValue(int value) {this.value=value;}
        public int getValue() {return value;}
    }

    @Test
    public void testSetIntoIntArray() {
        @Immutable int[] arr = new int[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = 1);
    }

    @Test
    public void testGetIntoIntArray() {
        @Local int[] arr = new int[] { 1, 2, 3, 4};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{int i = arr[2];}));
    }

    @Test
    public void testSetFloat() {
        @Immutable FloatBox obj = new FloatBox(5);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(59));
    }

    @Test
    public void testGetFloat() {
        @Local FloatBox obj = new FloatBox(5);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class FloatBox {
        float value;
        FloatBox(float value) {setValue(value);}
        public void setValue(float value) {this.value=value;}
        public float getValue() {return value;}
    }

    @Test
    public void testSetIntoFloatArray() {
        @Immutable float[] arr = new float[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = 1);
    }

    @Test
    public void testGetIntoFloatArray() {
        @Local float[] arr = new float[] { 1, 2, 3, 4};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{float i = arr[2];}));
    }

    @Test
    public void testSetLong() {
        @Immutable LongBox obj = new LongBox(5);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(59));
    }

    @Test
    public void testGetLong() {
        @Local LongBox obj = new LongBox(5);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class LongBox {
        long value;
        LongBox(long value) {setValue(value);}
        public void setValue(long value) {this.value=value;}
        public long getValue() {return value;}
    }

    @Test
    public void testSetIntoLongArray() {
        @Immutable long[] arr = new long[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = 1);
    }

    @Test
    public void testGetIntoLongArray() {
        @Local long[] arr = new long[] { 1, 2, 3, 4};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{long i = arr[2];}));
    }

    @Test
    public void testSetDouble() {
        @Immutable DoubleBox obj = new DoubleBox(5);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(59));
    }

    @Test
    public void testGetDouble() {
        @Local DoubleBox obj = new DoubleBox(5);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class DoubleBox {
        double value;
        DoubleBox(double value) {setValue(value);}
        public void setValue(double value) {this.value=value;}
        public double getValue() {return value;}
    }

    @Test
    public void testSetIntoDoubleArray() {
        @Immutable double[] arr = new double[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = 1);
    }

    @Test
    public void testGetIntoDoubleArray() {
        @Local double[] arr = new double[] { 1, 2, 3, 4};
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{double i = arr[2];}));
    }

    @Test
    public void testSetObject() {
        @Immutable ObjectBox obj = new ObjectBox(new Object());
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(new Object()));
    }

    @Test
    public void testGetObject() {
        @Local ObjectBox obj = new ObjectBox(new Object());
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class ObjectBox {
        Object value;
        ObjectBox(Object value) {setValue(value);}
        public void setValue(Object value) {this.value=value;}
        public Object getValue() {return value;}
    }

    @Test
    public void testSetIntoObjectArray() {
        @Immutable Object[] arr = new Object[10];
        assertThrows(DalaCapabilityViolationException.class, () -> arr[2] = new Object());
    }

    @Test
    public void testGetIntoObjectArray() {
        @Local Object[] arr = new Object[] { new Object(), new Object(), new Object() };
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->{Object i = arr[2];}));
    }

    @Test
    public void testSetIntArray() {
        @Immutable IntArrayBox obj = new IntArrayBox(new int[10]);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(new int[15]));
    }

    @Test
    public void testGetIntArray() {
        @Local IntArrayBox obj = new IntArrayBox(new int[10]);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class IntArrayBox {
        int[] value;
        IntArrayBox(int[] value) {setValue(value);}
        public void setValue(int[] value) {this.value=value;}
        public int[] getValue() {return value;}
    }

    @Test
    public void testSetObjectArray() {
        @Immutable ObjectArrayBox obj = new ObjectArrayBox(new Object[10]);
        assertThrows(DalaCapabilityViolationException.class, () -> obj.setValue(new Object[15]));
    }

    @Test
    public void testGetObjectArray() {
        @Local ObjectArrayBox obj = new ObjectArrayBox(new Object[10]);
        assertInstanceOf(DalaCapabilityViolationException.class, runInOtherThread(()->obj.getValue()));
    }

    static class ObjectArrayBox {
        Object[] value;
        ObjectArrayBox(Object[] value) {setValue(value);}
        public void setValue(Object[] value) {this.value=value;}
        public Object[] getValue() {return value;}
    }

}
