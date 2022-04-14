package org.apache.commons.compress.utils;

import java.io.*;

public final class ByteUtils
{
    private ByteUtils() {
    }
    
    public static long fromLittleEndian(final byte[] bytes) {
        return fromLittleEndian(bytes, 0, bytes.length);
    }
    
    public static long fromLittleEndian(final byte[] bytes, final int off, final int length) {
        checkReadLength(length);
        long l = 0L;
        for (int i = 0; i < length; ++i) {
            l |= ((long)bytes[off + i] & 0xFFL) << 8 * i;
        }
        return l;
    }
    
    public static long fromLittleEndian(final InputStream in, final int length) throws IOException {
        checkReadLength(length);
        long l = 0L;
        for (int i = 0; i < length; ++i) {
            final long b = in.read();
            if (b == -1L) {
                throw new IOException("premature end of data");
            }
            l |= b << i * 8;
        }
        return l;
    }
    
    public static long fromLittleEndian(final ByteSupplier supplier, final int length) throws IOException {
        checkReadLength(length);
        long l = 0L;
        for (int i = 0; i < length; ++i) {
            final long b = supplier.getAsByte();
            if (b == -1L) {
                throw new IOException("premature end of data");
            }
            l |= b << i * 8;
        }
        return l;
    }
    
    public static long fromLittleEndian(final DataInput in, final int length) throws IOException {
        checkReadLength(length);
        long l = 0L;
        for (int i = 0; i < length; ++i) {
            final long b = in.readUnsignedByte();
            l |= b << i * 8;
        }
        return l;
    }
    
    public static void toLittleEndian(final byte[] b, final long value, final int off, final int length) {
        long num = value;
        for (int i = 0; i < length; ++i) {
            b[off + i] = (byte)(num & 0xFFL);
            num >>= 8;
        }
    }
    
    public static void toLittleEndian(final OutputStream out, final long value, final int length) throws IOException {
        long num = value;
        for (int i = 0; i < length; ++i) {
            out.write((int)(num & 0xFFL));
            num >>= 8;
        }
    }
    
    public static void toLittleEndian(final ByteConsumer consumer, final long value, final int length) throws IOException {
        long num = value;
        for (int i = 0; i < length; ++i) {
            consumer.accept((int)(num & 0xFFL));
            num >>= 8;
        }
    }
    
    public static void toLittleEndian(final DataOutput out, final long value, final int length) throws IOException {
        long num = value;
        for (int i = 0; i < length; ++i) {
            out.write((int)(num & 0xFFL));
            num >>= 8;
        }
    }
    
    private static final void checkReadLength(final int length) {
        if (length > 8) {
            throw new IllegalArgumentException("can't read more than eight bytes into a long value");
        }
    }
    
    public static class InputStreamByteSupplier implements ByteSupplier
    {
        private final InputStream is;
        
        public InputStreamByteSupplier(final InputStream is) {
            this.is = is;
        }
        
        @Override
        public int getAsByte() throws IOException {
            return this.is.read();
        }
    }
    
    public static class OutputStreamByteConsumer implements ByteConsumer
    {
        private final OutputStream os;
        
        public OutputStreamByteConsumer(final OutputStream os) {
            this.os = os;
        }
        
        @Override
        public void accept(final int b) throws IOException {
            this.os.write(b);
        }
    }
    
    public interface ByteConsumer
    {
        void accept(final int p0) throws IOException;
    }
    
    public interface ByteSupplier
    {
        int getAsByte() throws IOException;
    }
}
