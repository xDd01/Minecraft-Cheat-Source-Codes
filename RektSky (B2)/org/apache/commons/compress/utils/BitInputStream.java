package org.apache.commons.compress.utils;

import java.nio.*;
import java.io.*;

public class BitInputStream implements Closeable
{
    private static final int MAXIMUM_CACHE_SIZE = 63;
    private static final long[] MASKS;
    private final CountingInputStream in;
    private final ByteOrder byteOrder;
    private long bitsCached;
    private int bitsCachedSize;
    
    public BitInputStream(final InputStream in, final ByteOrder byteOrder) {
        this.bitsCached = 0L;
        this.bitsCachedSize = 0;
        this.in = new CountingInputStream(in);
        this.byteOrder = byteOrder;
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    public void clearBitCache() {
        this.bitsCached = 0L;
        this.bitsCachedSize = 0;
    }
    
    public long readBits(final int count) throws IOException {
        if (count < 0 || count > 63) {
            throw new IllegalArgumentException("count must not be negative or greater than 63");
        }
        if (this.ensureCache(count)) {
            return -1L;
        }
        if (this.bitsCachedSize < count) {
            return this.processBitsGreater57(count);
        }
        return this.readCachedBits(count);
    }
    
    public int bitsCached() {
        return this.bitsCachedSize;
    }
    
    public long bitsAvailable() throws IOException {
        return this.bitsCachedSize + 8L * this.in.available();
    }
    
    public void alignWithByteBoundary() {
        final int toSkip = this.bitsCachedSize % 8;
        if (toSkip > 0) {
            this.readCachedBits(toSkip);
        }
    }
    
    public long getBytesRead() {
        return this.in.getBytesRead();
    }
    
    private long processBitsGreater57(final int count) throws IOException {
        int overflowBits = 0;
        long overflow = 0L;
        final int bitsToAddCount = count - this.bitsCachedSize;
        overflowBits = 8 - bitsToAddCount;
        final long nextByte = this.in.read();
        if (nextByte < 0L) {
            return nextByte;
        }
        if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            final long bitsToAdd = nextByte & BitInputStream.MASKS[bitsToAddCount];
            this.bitsCached |= bitsToAdd << this.bitsCachedSize;
            overflow = (nextByte >>> bitsToAddCount & BitInputStream.MASKS[overflowBits]);
        }
        else {
            this.bitsCached <<= bitsToAddCount;
            final long bitsToAdd = nextByte >>> overflowBits & BitInputStream.MASKS[bitsToAddCount];
            this.bitsCached |= bitsToAdd;
            overflow = (nextByte & BitInputStream.MASKS[overflowBits]);
        }
        final long bitsOut = this.bitsCached & BitInputStream.MASKS[count];
        this.bitsCached = overflow;
        this.bitsCachedSize = overflowBits;
        return bitsOut;
    }
    
    private long readCachedBits(final int count) {
        long bitsOut;
        if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            bitsOut = (this.bitsCached & BitInputStream.MASKS[count]);
            this.bitsCached >>>= count;
        }
        else {
            bitsOut = (this.bitsCached >> this.bitsCachedSize - count & BitInputStream.MASKS[count]);
        }
        this.bitsCachedSize -= count;
        return bitsOut;
    }
    
    private boolean ensureCache(final int count) throws IOException {
        while (this.bitsCachedSize < count && this.bitsCachedSize < 57) {
            final long nextByte = this.in.read();
            if (nextByte < 0L) {
                return true;
            }
            if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
                this.bitsCached |= nextByte << this.bitsCachedSize;
            }
            else {
                this.bitsCached <<= 8;
                this.bitsCached |= nextByte;
            }
            this.bitsCachedSize += 8;
        }
        return false;
    }
    
    static {
        MASKS = new long[64];
        for (int i = 1; i <= 63; ++i) {
            BitInputStream.MASKS[i] = (BitInputStream.MASKS[i - 1] << 1) + 1L;
        }
    }
}
