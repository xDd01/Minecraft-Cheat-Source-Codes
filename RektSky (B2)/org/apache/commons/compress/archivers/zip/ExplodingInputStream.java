package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.utils.*;
import java.io.*;

class ExplodingInputStream extends InputStream implements InputStreamStatistics
{
    private final InputStream in;
    private BitStream bits;
    private final int dictionarySize;
    private final int numberOfTrees;
    private final int minimumMatchLength;
    private BinaryTree literalTree;
    private BinaryTree lengthTree;
    private BinaryTree distanceTree;
    private final CircularBuffer buffer;
    private long uncompressedCount;
    private long treeSizes;
    
    public ExplodingInputStream(final int dictionarySize, final int numberOfTrees, final InputStream in) {
        this.buffer = new CircularBuffer(32768);
        this.uncompressedCount = 0L;
        this.treeSizes = 0L;
        if (dictionarySize != 4096 && dictionarySize != 8192) {
            throw new IllegalArgumentException("The dictionary size must be 4096 or 8192");
        }
        if (numberOfTrees != 2 && numberOfTrees != 3) {
            throw new IllegalArgumentException("The number of trees must be 2 or 3");
        }
        this.dictionarySize = dictionarySize;
        this.numberOfTrees = numberOfTrees;
        this.minimumMatchLength = numberOfTrees;
        this.in = in;
    }
    
    private void init() throws IOException {
        if (this.bits == null) {
            try (final CountingInputStream i = new CountingInputStream(this.in) {
                @Override
                public void close() {
                }
            }) {
                if (this.numberOfTrees == 3) {
                    this.literalTree = BinaryTree.decode(i, 256);
                }
                this.lengthTree = BinaryTree.decode(i, 64);
                this.distanceTree = BinaryTree.decode(i, 64);
                this.treeSizes += i.getBytesRead();
            }
            this.bits = new BitStream(this.in);
        }
    }
    
    @Override
    public int read() throws IOException {
        if (!this.buffer.available()) {
            this.fillBuffer();
        }
        final int ret = this.buffer.get();
        if (ret > -1) {
            ++this.uncompressedCount;
        }
        return ret;
    }
    
    @Override
    public long getCompressedCount() {
        return this.bits.getBytesRead() + this.treeSizes;
    }
    
    @Override
    public long getUncompressedCount() {
        return this.uncompressedCount;
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    private void fillBuffer() throws IOException {
        this.init();
        final int bit = this.bits.nextBit();
        if (bit == 1) {
            int literal;
            if (this.literalTree != null) {
                literal = this.literalTree.read(this.bits);
            }
            else {
                literal = this.bits.nextByte();
            }
            if (literal == -1) {
                return;
            }
            this.buffer.put(literal);
        }
        else if (bit == 0) {
            final int distanceLowSize = (this.dictionarySize == 4096) ? 6 : 7;
            final int distanceLow = (int)this.bits.nextBits(distanceLowSize);
            final int distanceHigh = this.distanceTree.read(this.bits);
            if (distanceHigh == -1 && distanceLow <= 0) {
                return;
            }
            final int distance = distanceHigh << distanceLowSize | distanceLow;
            int length = this.lengthTree.read(this.bits);
            if (length == 63) {
                length += (int)this.bits.nextBits(8);
            }
            length += this.minimumMatchLength;
            this.buffer.copy(distance + 1, length);
        }
    }
}
