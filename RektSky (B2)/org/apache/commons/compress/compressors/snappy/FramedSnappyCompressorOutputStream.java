package org.apache.commons.compress.compressors.snappy;

import org.apache.commons.compress.compressors.*;
import org.apache.commons.compress.compressors.lz77support.*;
import org.apache.commons.compress.utils.*;
import java.io.*;

public class FramedSnappyCompressorOutputStream extends CompressorOutputStream
{
    private static final int MAX_COMPRESSED_BUFFER_SIZE = 65536;
    private final OutputStream out;
    private final Parameters params;
    private final PureJavaCrc32C checksum;
    private final byte[] oneByte;
    private final byte[] buffer;
    private int currentIndex;
    private final ByteUtils.ByteConsumer consumer;
    
    public FramedSnappyCompressorOutputStream(final OutputStream out) throws IOException {
        this(out, SnappyCompressorOutputStream.createParameterBuilder(32768).build());
    }
    
    public FramedSnappyCompressorOutputStream(final OutputStream out, final Parameters params) throws IOException {
        this.checksum = new PureJavaCrc32C();
        this.oneByte = new byte[1];
        this.buffer = new byte[65536];
        this.currentIndex = 0;
        this.out = out;
        this.params = params;
        this.consumer = new ByteUtils.OutputStreamByteConsumer(out);
        out.write(FramedSnappyCompressorInputStream.SZ_SIGNATURE);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.oneByte[0] = (byte)(b & 0xFF);
        this.write(this.oneByte);
    }
    
    @Override
    public void write(final byte[] data, int off, int len) throws IOException {
        if (this.currentIndex + len > 65536) {
            this.flushBuffer();
            while (len > 65536) {
                System.arraycopy(data, off, this.buffer, 0, 65536);
                off += 65536;
                len -= 65536;
                this.currentIndex = 65536;
                this.flushBuffer();
            }
        }
        System.arraycopy(data, off, this.buffer, this.currentIndex, len);
        this.currentIndex += len;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.finish();
        }
        finally {
            this.out.close();
        }
    }
    
    public void finish() throws IOException {
        if (this.currentIndex > 0) {
            this.flushBuffer();
        }
    }
    
    private void flushBuffer() throws IOException {
        this.out.write(0);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final OutputStream o = new SnappyCompressorOutputStream(baos, this.currentIndex, this.params)) {
            o.write(this.buffer, 0, this.currentIndex);
        }
        final byte[] b = baos.toByteArray();
        this.writeLittleEndian(3, b.length + 4L);
        this.writeCrc();
        this.out.write(b);
        this.currentIndex = 0;
    }
    
    private void writeLittleEndian(final int numBytes, final long num) throws IOException {
        ByteUtils.toLittleEndian(this.consumer, num, numBytes);
    }
    
    private void writeCrc() throws IOException {
        this.checksum.update(this.buffer, 0, this.currentIndex);
        this.writeLittleEndian(4, mask(this.checksum.getValue()));
        this.checksum.reset();
    }
    
    static long mask(long x) {
        x = (x >> 15 | x << 17);
        x += 2726488792L;
        x &= 0xFFFFFFFFL;
        return x;
    }
}
