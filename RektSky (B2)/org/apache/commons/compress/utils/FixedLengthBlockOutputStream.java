package org.apache.commons.compress.utils;

import java.util.concurrent.atomic.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;

public class FixedLengthBlockOutputStream extends OutputStream implements WritableByteChannel
{
    private final WritableByteChannel out;
    private final int blockSize;
    private final ByteBuffer buffer;
    private final AtomicBoolean closed;
    
    public FixedLengthBlockOutputStream(final OutputStream os, final int blockSize) {
        this.closed = new AtomicBoolean(false);
        if (os instanceof FileOutputStream) {
            final FileOutputStream fileOutputStream = (FileOutputStream)os;
            this.out = fileOutputStream.getChannel();
            this.buffer = ByteBuffer.allocateDirect(blockSize);
        }
        else {
            this.out = new BufferAtATimeOutputChannel(os);
            this.buffer = ByteBuffer.allocate(blockSize);
        }
        this.blockSize = blockSize;
    }
    
    public FixedLengthBlockOutputStream(final WritableByteChannel out, final int blockSize) {
        this.closed = new AtomicBoolean(false);
        this.out = out;
        this.blockSize = blockSize;
        this.buffer = ByteBuffer.allocateDirect(blockSize);
    }
    
    private void maybeFlush() throws IOException {
        if (!this.buffer.hasRemaining()) {
            this.writeBlock();
        }
    }
    
    private void writeBlock() throws IOException {
        this.buffer.flip();
        final int i = this.out.write(this.buffer);
        final boolean hasRemaining = this.buffer.hasRemaining();
        if (i != this.blockSize || hasRemaining) {
            final String msg = String.format("Failed to write %,d bytes atomically. Only wrote  %,d", this.blockSize, i);
            throw new IOException(msg);
        }
        this.buffer.clear();
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        this.buffer.put((byte)b);
        this.maybeFlush();
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int length) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        int n;
        for (int off = offset, len = length; len > 0; len -= n, off += n) {
            n = Math.min(len, this.buffer.remaining());
            this.buffer.put(b, off, n);
            this.maybeFlush();
        }
    }
    
    @Override
    public int write(final ByteBuffer src) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        final int srcRemaining = src.remaining();
        if (srcRemaining < this.buffer.remaining()) {
            this.buffer.put(src);
        }
        else {
            int srcLeft = srcRemaining;
            final int savedLimit = src.limit();
            if (this.buffer.position() != 0) {
                final int n = this.buffer.remaining();
                src.limit(src.position() + n);
                this.buffer.put(src);
                this.writeBlock();
                srcLeft -= n;
            }
            while (srcLeft >= this.blockSize) {
                src.limit(src.position() + this.blockSize);
                this.out.write(src);
                srcLeft -= this.blockSize;
            }
            src.limit(savedLimit);
            this.buffer.put(src);
        }
        return srcRemaining;
    }
    
    @Override
    public boolean isOpen() {
        if (!this.out.isOpen()) {
            this.closed.set(true);
        }
        return !this.closed.get();
    }
    
    public void flushBlock() throws IOException {
        if (this.buffer.position() != 0) {
            this.padBlock();
            this.writeBlock();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed.compareAndSet(false, true)) {
            try {
                this.flushBlock();
            }
            finally {
                this.out.close();
            }
        }
    }
    
    private void padBlock() {
        this.buffer.order(ByteOrder.nativeOrder());
        int bytesToWrite = this.buffer.remaining();
        if (bytesToWrite > 8) {
            final int align = this.buffer.position() & 0x7;
            if (align != 0) {
                final int limit = 8 - align;
                for (int i = 0; i < limit; ++i) {
                    this.buffer.put((byte)0);
                }
                bytesToWrite -= limit;
            }
            while (bytesToWrite >= 8) {
                this.buffer.putLong(0L);
                bytesToWrite -= 8;
            }
        }
        while (this.buffer.hasRemaining()) {
            this.buffer.put((byte)0);
        }
    }
    
    private static class BufferAtATimeOutputChannel implements WritableByteChannel
    {
        private final OutputStream out;
        private final AtomicBoolean closed;
        
        private BufferAtATimeOutputChannel(final OutputStream out) {
            this.closed = new AtomicBoolean(false);
            this.out = out;
        }
        
        @Override
        public int write(final ByteBuffer buffer) throws IOException {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (!buffer.hasArray()) {
                throw new IllegalArgumentException("direct buffer somehow written to BufferAtATimeOutputChannel");
            }
            try {
                final int pos = buffer.position();
                final int len = buffer.limit() - pos;
                this.out.write(buffer.array(), buffer.arrayOffset() + pos, len);
                buffer.position(buffer.limit());
                return len;
            }
            catch (IOException e) {
                try {
                    this.close();
                }
                catch (IOException ex) {}
                throw e;
            }
        }
        
        @Override
        public boolean isOpen() {
            return !this.closed.get();
        }
        
        @Override
        public void close() throws IOException {
            if (this.closed.compareAndSet(false, true)) {
                this.out.close();
            }
        }
    }
}
