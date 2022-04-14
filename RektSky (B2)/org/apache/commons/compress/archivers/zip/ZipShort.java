package org.apache.commons.compress.archivers.zip;

import java.io.*;
import org.apache.commons.compress.utils.*;

public final class ZipShort implements Cloneable, Serializable
{
    public static final ZipShort ZERO;
    private static final long serialVersionUID = 1L;
    private final int value;
    
    public ZipShort(final int value) {
        this.value = value;
    }
    
    public ZipShort(final byte[] bytes) {
        this(bytes, 0);
    }
    
    public ZipShort(final byte[] bytes, final int offset) {
        this.value = getValue(bytes, offset);
    }
    
    public byte[] getBytes() {
        final byte[] result = new byte[2];
        ByteUtils.toLittleEndian(result, this.value, 0, 2);
        return result;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static byte[] getBytes(final int value) {
        final byte[] result = new byte[2];
        putShort(value, result, 0);
        return result;
    }
    
    public static void putShort(final int value, final byte[] buf, final int offset) {
        ByteUtils.toLittleEndian(buf, value, offset, 2);
    }
    
    public static int getValue(final byte[] bytes, final int offset) {
        return (int)ByteUtils.fromLittleEndian(bytes, offset, 2);
    }
    
    public static int getValue(final byte[] bytes) {
        return getValue(bytes, 0);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof ZipShort && this.value == ((ZipShort)o).getValue();
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }
    
    @Override
    public String toString() {
        return "ZipShort value: " + this.value;
    }
    
    static {
        ZERO = new ZipShort(0);
    }
}
