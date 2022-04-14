package org.apache.commons.compress.compressors.lz77support;

import org.apache.commons.compress.compressors.*;
import java.io.*;
import org.apache.commons.compress.utils.*;
import java.util.*;

public abstract class AbstractLZ77CompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private final int windowSize;
    private final byte[] buf;
    private int writeIndex;
    private int readIndex;
    private final CountingInputStream in;
    private long bytesRemaining;
    private int backReferenceOffset;
    private int size;
    private final byte[] oneByte;
    protected final ByteUtils.ByteSupplier supplier;
    
    public AbstractLZ77CompressorInputStream(final InputStream is, final int windowSize) throws IOException {
        this.size = 0;
        this.oneByte = new byte[1];
        this.supplier = new ByteUtils.ByteSupplier() {
            @Override
            public int getAsByte() throws IOException {
                return AbstractLZ77CompressorInputStream.this.readOneByte();
            }
        };
        this.in = new CountingInputStream(is);
        this.windowSize = windowSize;
        this.buf = new byte[3 * windowSize];
        final int n = 0;
        this.readIndex = n;
        this.writeIndex = n;
        this.bytesRemaining = 0L;
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.oneByte, 0, 1) == -1) ? -1 : (this.oneByte[0] & 0xFF);
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public int available() {
        return this.writeIndex - this.readIndex;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void prefill(final byte[] data) {
        if (this.writeIndex != 0) {
            throw new IllegalStateException("the stream has already been read from, can't prefill anymore");
        }
        final int len = Math.min(this.windowSize, data.length);
        System.arraycopy(data, data.length - len, this.buf, 0, len);
        this.writeIndex += len;
        this.readIndex += len;
    }
    
    @Override
    public long getCompressedCount() {
        return this.in.getBytesRead();
    }
    
    protected final void startLiteral(final long length) {
        this.bytesRemaining = length;
    }
    
    protected final boolean hasMoreDataInBlock() {
        return this.bytesRemaining > 0L;
    }
    
    protected final int readLiteral(final byte[] b, final int off, final int len) throws IOException {
        final int avail = this.available();
        if (len > avail) {
            this.tryToReadLiteral(len - avail);
        }
        return this.readFromBuffer(b, off, len);
    }
    
    private void tryToReadLiteral(final int bytesToRead) throws IOException {
        final int reallyTryToRead = Math.min((int)Math.min(bytesToRead, this.bytesRemaining), this.buf.length - this.writeIndex);
        final int bytesRead = (reallyTryToRead > 0) ? IOUtils.readFully(this.in, this.buf, this.writeIndex, reallyTryToRead) : 0;
        this.count(bytesRead);
        if (reallyTryToRead != bytesRead) {
            throw new IOException("Premature end of stream reading literal");
        }
        this.writeIndex += reallyTryToRead;
        this.bytesRemaining -= reallyTryToRead;
    }
    
    private int readFromBuffer(final byte[] b, final int off, final int len) {
        final int readable = Math.min(len, this.available());
        if (readable > 0) {
            System.arraycopy(this.buf, this.readIndex, b, off, readable);
            this.readIndex += readable;
            if (this.readIndex > 2 * this.windowSize) {
                this.slideBuffer();
            }
        }
        this.size += readable;
        return readable;
    }
    
    private void slideBuffer() {
        System.arraycopy(this.buf, this.windowSize, this.buf, 0, this.windowSize * 2);
        this.writeIndex -= this.windowSize;
        this.readIndex -= this.windowSize;
    }
    
    protected final void startBackReference(final int offset, final long length) {
        this.backReferenceOffset = offset;
        this.bytesRemaining = length;
    }
    
    protected final int readBackReference(final byte[] b, final int off, final int len) {
        final int avail = this.available();
        if (len > avail) {
            this.tryToCopy(len - avail);
        }
        return this.readFromBuffer(b, off, len);
    }
    
    private void tryToCopy(final int bytesToCopy) {
        final int copy = Math.min((int)Math.min(bytesToCopy, this.bytesRemaining), this.buf.length - this.writeIndex);
        if (copy != 0) {
            if (this.backReferenceOffset == 1) {
                final byte last = this.buf[this.writeIndex - 1];
                Arrays.fill(this.buf, this.writeIndex, this.writeIndex + copy, last);
                this.writeIndex += copy;
            }
            else if (copy < this.backReferenceOffset) {
                System.arraycopy(this.buf, this.writeIndex - this.backReferenceOffset, this.buf, this.writeIndex, copy);
                this.writeIndex += copy;
            }
            else {
                final int fullRots = copy / this.backReferenceOffset;
                for (int i = 0; i < fullRots; ++i) {
                    System.arraycopy(this.buf, this.writeIndex - this.backReferenceOffset, this.buf, this.writeIndex, this.backReferenceOffset);
                    this.writeIndex += this.backReferenceOffset;
                }
                final int pad = copy - this.backReferenceOffset * fullRots;
                if (pad > 0) {
                    System.arraycopy(this.buf, this.writeIndex - this.backReferenceOffset, this.buf, this.writeIndex, pad);
                    this.writeIndex += pad;
                }
            }
        }
        this.bytesRemaining -= copy;
    }
    
    protected final int readOneByte() throws IOException {
        final int b = this.in.read();
        if (b != -1) {
            this.count(1);
            return b & 0xFF;
        }
        return -1;
    }
}
