package org.apache.commons.io.input;

import java.nio.*;
import java.nio.charset.*;
import java.io.*;

public class CharSequenceInputStream extends InputStream
{
    private static final int BUFFER_SIZE = 2048;
    private static final int NO_MARK = -1;
    private final CharsetEncoder encoder;
    private final CharBuffer cbuf;
    private final ByteBuffer bbuf;
    private int mark_cbuf;
    private int mark_bbuf;
    
    public CharSequenceInputStream(final CharSequence cs, final Charset charset, final int bufferSize) {
        this.encoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        final float maxBytesPerChar = this.encoder.maxBytesPerChar();
        if (bufferSize < maxBytesPerChar) {
            throw new IllegalArgumentException("Buffer size " + bufferSize + " is less than maxBytesPerChar " + maxBytesPerChar);
        }
        (this.bbuf = ByteBuffer.allocate(bufferSize)).flip();
        this.cbuf = CharBuffer.wrap(cs);
        this.mark_cbuf = -1;
        this.mark_bbuf = -1;
    }
    
    public CharSequenceInputStream(final CharSequence cs, final String charset, final int bufferSize) {
        this(cs, Charset.forName(charset), bufferSize);
    }
    
    public CharSequenceInputStream(final CharSequence cs, final Charset charset) {
        this(cs, charset, 2048);
    }
    
    public CharSequenceInputStream(final CharSequence cs, final String charset) {
        this(cs, charset, 2048);
    }
    
    private void fillBuffer() throws CharacterCodingException {
        this.bbuf.compact();
        final CoderResult result = this.encoder.encode(this.cbuf, this.bbuf, true);
        if (result.isError()) {
            result.throwException();
        }
        this.bbuf.flip();
    }
    
    @Override
    public int read(final byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException("Byte array is null");
        }
        if (len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException("Array Size=" + b.length + ", offset=" + off + ", length=" + len);
        }
        if (len == 0) {
            return 0;
        }
        if (!this.bbuf.hasRemaining() && !this.cbuf.hasRemaining()) {
            return -1;
        }
        int bytesRead = 0;
        while (len > 0) {
            if (this.bbuf.hasRemaining()) {
                final int chunk = Math.min(this.bbuf.remaining(), len);
                this.bbuf.get(b, off, chunk);
                off += chunk;
                len -= chunk;
                bytesRead += chunk;
            }
            else {
                this.fillBuffer();
                if (!this.bbuf.hasRemaining() && !this.cbuf.hasRemaining()) {
                    break;
                }
                continue;
            }
        }
        return (bytesRead == 0 && !this.cbuf.hasRemaining()) ? -1 : bytesRead;
    }
    
    @Override
    public int read() throws IOException {
        while (!this.bbuf.hasRemaining()) {
            this.fillBuffer();
            if (!this.bbuf.hasRemaining() && !this.cbuf.hasRemaining()) {
                return -1;
            }
        }
        return this.bbuf.get() & 0xFF;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public long skip(long n) throws IOException {
        long skipped;
        for (skipped = 0L; n > 0L && this.available() > 0; --n, ++skipped) {
            this.read();
        }
        return skipped;
    }
    
    @Override
    public int available() throws IOException {
        return this.bbuf.remaining() + this.cbuf.remaining();
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.mark_cbuf = this.cbuf.position();
        this.mark_bbuf = this.bbuf.position();
        this.cbuf.mark();
        this.bbuf.mark();
    }
    
    @Override
    public synchronized void reset() throws IOException {
        if (this.mark_cbuf != -1) {
            if (this.cbuf.position() != 0) {
                this.encoder.reset();
                this.cbuf.rewind();
                this.bbuf.rewind();
                this.bbuf.limit(0);
                while (this.cbuf.position() < this.mark_cbuf) {
                    this.bbuf.rewind();
                    this.bbuf.limit(0);
                    this.fillBuffer();
                }
            }
            if (this.cbuf.position() != this.mark_cbuf) {
                throw new IllegalStateException("Unexpected CharBuffer postion: actual=" + this.cbuf.position() + " expected=" + this.mark_cbuf);
            }
            this.bbuf.position(this.mark_bbuf);
            this.mark_cbuf = -1;
            this.mark_bbuf = -1;
        }
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
}
