package org.apache.commons.compress.archivers.sevenz;

import java.nio.*;
import java.nio.channels.*;
import java.io.*;

class BoundedSeekableByteChannelInputStream extends InputStream
{
    private static final int MAX_BUF_LEN = 8192;
    private final ByteBuffer buffer;
    private final SeekableByteChannel channel;
    private long bytesRemaining;
    
    public BoundedSeekableByteChannelInputStream(final SeekableByteChannel channel, final long size) {
        this.channel = channel;
        this.bytesRemaining = size;
        if (size < 8192L && size > 0L) {
            this.buffer = ByteBuffer.allocate((int)size);
        }
        else {
            this.buffer = ByteBuffer.allocate(8192);
        }
    }
    
    @Override
    public int read() throws IOException {
        if (this.bytesRemaining <= 0L) {
            return -1;
        }
        --this.bytesRemaining;
        final int read = this.read(1);
        if (read < 0) {
            return read;
        }
        return this.buffer.get() & 0xFF;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.bytesRemaining == 0L) {
            return -1;
        }
        int bytesToRead = len;
        if (bytesToRead > this.bytesRemaining) {
            bytesToRead = (int)this.bytesRemaining;
        }
        ByteBuffer buf;
        int bytesRead;
        if (bytesToRead <= this.buffer.capacity()) {
            buf = this.buffer;
            bytesRead = this.read(bytesToRead);
        }
        else {
            buf = ByteBuffer.allocate(bytesToRead);
            bytesRead = this.channel.read(buf);
            buf.flip();
        }
        if (bytesRead >= 0) {
            buf.get(b, off, bytesRead);
            this.bytesRemaining -= bytesRead;
        }
        return bytesRead;
    }
    
    private int read(final int len) throws IOException {
        this.buffer.rewind().limit(len);
        final int read = this.channel.read(this.buffer);
        this.buffer.flip();
        return read;
    }
    
    @Override
    public void close() {
    }
}
