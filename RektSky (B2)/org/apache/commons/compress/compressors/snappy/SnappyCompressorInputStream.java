package org.apache.commons.compress.compressors.snappy;

import org.apache.commons.compress.compressors.lz77support.*;
import java.io.*;
import org.apache.commons.compress.utils.*;

public class SnappyCompressorInputStream extends AbstractLZ77CompressorInputStream
{
    private static final int TAG_MASK = 3;
    public static final int DEFAULT_BLOCK_SIZE = 32768;
    private final int size;
    private int uncompressedBytesRemaining;
    private State state;
    private boolean endReached;
    
    public SnappyCompressorInputStream(final InputStream is) throws IOException {
        this(is, 32768);
    }
    
    public SnappyCompressorInputStream(final InputStream is, final int blockSize) throws IOException {
        super(is, blockSize);
        this.state = State.NO_BLOCK;
        this.endReached = false;
        final int n = (int)this.readSize();
        this.size = n;
        this.uncompressedBytesRemaining = n;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.endReached) {
            return -1;
        }
        switch (this.state) {
            case NO_BLOCK: {
                this.fill();
                return this.read(b, off, len);
            }
            case IN_LITERAL: {
                final int litLen = this.readLiteral(b, off, len);
                if (!this.hasMoreDataInBlock()) {
                    this.state = State.NO_BLOCK;
                }
                return (litLen > 0) ? litLen : this.read(b, off, len);
            }
            case IN_BACK_REFERENCE: {
                final int backReferenceLen = this.readBackReference(b, off, len);
                if (!this.hasMoreDataInBlock()) {
                    this.state = State.NO_BLOCK;
                }
                return (backReferenceLen > 0) ? backReferenceLen : this.read(b, off, len);
            }
            default: {
                throw new IOException("Unknown stream state " + this.state);
            }
        }
    }
    
    private void fill() throws IOException {
        if (this.uncompressedBytesRemaining == 0) {
            this.endReached = true;
            return;
        }
        int b = this.readOneByte();
        if (b == -1) {
            throw new IOException("Premature end of stream reading block start");
        }
        int length = 0;
        int offset = 0;
        switch (b & 0x3) {
            case 0: {
                length = this.readLiteralLength(b);
                this.uncompressedBytesRemaining -= length;
                this.startLiteral(length);
                this.state = State.IN_LITERAL;
                break;
            }
            case 1: {
                length = 4 + (b >> 2 & 0x7);
                this.uncompressedBytesRemaining -= length;
                offset = (b & 0xE0) << 3;
                b = this.readOneByte();
                if (b == -1) {
                    throw new IOException("Premature end of stream reading back-reference length");
                }
                offset |= b;
                this.startBackReference(offset, length);
                this.state = State.IN_BACK_REFERENCE;
                break;
            }
            case 2: {
                length = (b >> 2) + 1;
                this.uncompressedBytesRemaining -= length;
                offset = (int)ByteUtils.fromLittleEndian(this.supplier, 2);
                this.startBackReference(offset, length);
                this.state = State.IN_BACK_REFERENCE;
                break;
            }
            case 3: {
                length = (b >> 2) + 1;
                this.uncompressedBytesRemaining -= length;
                offset = ((int)ByteUtils.fromLittleEndian(this.supplier, 4) & Integer.MAX_VALUE);
                this.startBackReference(offset, length);
                this.state = State.IN_BACK_REFERENCE;
                break;
            }
        }
    }
    
    private int readLiteralLength(final int b) throws IOException {
        int length = 0;
        switch (b >> 2) {
            case 60: {
                length = this.readOneByte();
                if (length == -1) {
                    throw new IOException("Premature end of stream reading literal length");
                }
                break;
            }
            case 61: {
                length = (int)ByteUtils.fromLittleEndian(this.supplier, 2);
                break;
            }
            case 62: {
                length = (int)ByteUtils.fromLittleEndian(this.supplier, 3);
                break;
            }
            case 63: {
                length = (int)ByteUtils.fromLittleEndian(this.supplier, 4);
                break;
            }
            default: {
                length = b >> 2;
                break;
            }
        }
        return length + 1;
    }
    
    private long readSize() throws IOException {
        int index = 0;
        long sz = 0L;
        int b = 0;
        do {
            b = this.readOneByte();
            if (b == -1) {
                throw new IOException("Premature end of stream reading size");
            }
            sz |= (b & 0x7F) << index++ * 7;
        } while (0x0 != (b & 0x80));
        return sz;
    }
    
    @Override
    public int getSize() {
        return this.size;
    }
    
    private enum State
    {
        NO_BLOCK, 
        IN_LITERAL, 
        IN_BACK_REFERENCE;
    }
}
