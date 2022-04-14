package org.apache.commons.compress.archivers.zip;

import java.io.*;
import org.apache.commons.compress.utils.*;

public final class ZipLong implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    private final long value;
    public static final ZipLong CFH_SIG;
    public static final ZipLong LFH_SIG;
    public static final ZipLong DD_SIG;
    static final ZipLong ZIP64_MAGIC;
    public static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER;
    public static final ZipLong AED_SIG;
    
    public ZipLong(final long value) {
        this.value = value;
    }
    
    public ZipLong(final int value) {
        this.value = value;
    }
    
    public ZipLong(final byte[] bytes) {
        this(bytes, 0);
    }
    
    public ZipLong(final byte[] bytes, final int offset) {
        this.value = getValue(bytes, offset);
    }
    
    public byte[] getBytes() {
        return getBytes(this.value);
    }
    
    public long getValue() {
        return this.value;
    }
    
    public int getIntValue() {
        return (int)this.value;
    }
    
    public static byte[] getBytes(final long value) {
        final byte[] result = new byte[4];
        putLong(value, result, 0);
        return result;
    }
    
    public static void putLong(final long value, final byte[] buf, final int offset) {
        ByteUtils.toLittleEndian(buf, value, offset, 4);
    }
    
    public void putLong(final byte[] buf, final int offset) {
        putLong(this.value, buf, offset);
    }
    
    public static long getValue(final byte[] bytes, final int offset) {
        return ByteUtils.fromLittleEndian(bytes, offset, 4);
    }
    
    public static long getValue(final byte[] bytes) {
        return getValue(bytes, 0);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof ZipLong && this.value == ((ZipLong)o).getValue();
    }
    
    @Override
    public int hashCode() {
        return (int)this.value;
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
        return "ZipLong value: " + this.value;
    }
    
    static {
        CFH_SIG = new ZipLong(33639248L);
        LFH_SIG = new ZipLong(67324752L);
        DD_SIG = new ZipLong(134695760L);
        ZIP64_MAGIC = new ZipLong(4294967295L);
        SINGLE_SEGMENT_SPLIT_MARKER = new ZipLong(808471376L);
        AED_SIG = new ZipLong(134630224L);
    }
}
