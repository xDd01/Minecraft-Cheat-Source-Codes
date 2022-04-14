package org.apache.http.client.entity;

import java.io.*;
import java.util.zip.*;

public class DeflateInputStream extends InputStream
{
    private final InputStream sourceStream;
    
    public DeflateInputStream(final InputStream wrapped) throws IOException {
        final PushbackInputStream pushback = new PushbackInputStream(wrapped, 2);
        final int i1 = pushback.read();
        final int i2 = pushback.read();
        if (i1 == -1 || i2 == -1) {
            throw new ZipException("Unexpected end of stream");
        }
        pushback.unread(i2);
        pushback.unread(i1);
        boolean nowrap = true;
        final int b1 = i1 & 0xFF;
        final int compressionMethod = b1 & 0xF;
        final int compressionInfo = b1 >> 4 & 0xF;
        final int b2 = i2 & 0xFF;
        if (compressionMethod == 8 && compressionInfo <= 7 && (b1 << 8 | b2) % 31 == 0) {
            nowrap = false;
        }
        this.sourceStream = new DeflateStream(pushback, new Inflater(nowrap));
    }
    
    @Override
    public int read() throws IOException {
        return this.sourceStream.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.sourceStream.read(b);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.sourceStream.read(b, off, len);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.sourceStream.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        return this.sourceStream.available();
    }
    
    @Override
    public void mark(final int readLimit) {
        this.sourceStream.mark(readLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.sourceStream.reset();
    }
    
    @Override
    public boolean markSupported() {
        return this.sourceStream.markSupported();
    }
    
    @Override
    public void close() throws IOException {
        this.sourceStream.close();
    }
    
    static class DeflateStream extends InflaterInputStream
    {
        private boolean closed;
        
        public DeflateStream(final InputStream in, final Inflater inflater) {
            super(in, inflater);
            this.closed = false;
        }
        
        @Override
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.inf.end();
            super.close();
        }
    }
}
