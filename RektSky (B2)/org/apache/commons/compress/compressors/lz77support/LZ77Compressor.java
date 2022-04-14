package org.apache.commons.compress.compressors.lz77support;

import java.util.*;
import java.io.*;

public class LZ77Compressor
{
    private static final Block THE_EOD;
    static final int NUMBER_OF_BYTES_IN_HASH = 3;
    private static final int NO_MATCH = -1;
    private final Parameters params;
    private final Callback callback;
    private final byte[] window;
    private final int[] head;
    private final int[] prev;
    private final int wMask;
    private boolean initialized;
    private int currentPosition;
    private int lookahead;
    private int insertHash;
    private int blockStart;
    private int matchStart;
    private int missedInserts;
    private static final int HASH_SIZE = 32768;
    private static final int HASH_MASK = 32767;
    private static final int H_SHIFT = 5;
    
    public LZ77Compressor(final Parameters params, final Callback callback) {
        this.initialized = false;
        this.lookahead = 0;
        this.insertHash = 0;
        this.blockStart = 0;
        this.matchStart = -1;
        this.missedInserts = 0;
        if (params == null) {
            throw new NullPointerException("params must not be null");
        }
        if (callback == null) {
            throw new NullPointerException("callback must not be null");
        }
        this.params = params;
        this.callback = callback;
        final int wSize = params.getWindowSize();
        this.window = new byte[wSize * 2];
        this.wMask = wSize - 1;
        Arrays.fill(this.head = new int[32768], -1);
        this.prev = new int[wSize];
    }
    
    public void compress(final byte[] data) throws IOException {
        this.compress(data, 0, data.length);
    }
    
    public void compress(final byte[] data, int off, int len) throws IOException {
        for (int wSize = this.params.getWindowSize(); len > wSize; len -= wSize) {
            this.doCompress(data, off, wSize);
            off += wSize;
        }
        if (len > 0) {
            this.doCompress(data, off, len);
        }
    }
    
    public void finish() throws IOException {
        if (this.blockStart != this.currentPosition || this.lookahead > 0) {
            this.currentPosition += this.lookahead;
            this.flushLiteralBlock();
        }
        this.callback.accept(LZ77Compressor.THE_EOD);
    }
    
    public void prefill(final byte[] data) {
        if (this.currentPosition != 0 || this.lookahead != 0) {
            throw new IllegalStateException("the compressor has already started to accept data, can't prefill anymore");
        }
        final int len = Math.min(this.params.getWindowSize(), data.length);
        System.arraycopy(data, data.length - len, this.window, 0, len);
        if (len >= 3) {
            this.initialize();
            for (int stop = len - 3 + 1, i = 0; i < stop; ++i) {
                this.insertString(i);
            }
            this.missedInserts = 2;
        }
        else {
            this.missedInserts = len;
        }
        final int n = len;
        this.currentPosition = n;
        this.blockStart = n;
    }
    
    private int nextHash(final int oldHash, final byte nextByte) {
        final int nextVal = nextByte & 0xFF;
        return (oldHash << 5 ^ nextVal) & 0x7FFF;
    }
    
    private void doCompress(final byte[] data, final int off, final int len) throws IOException {
        final int spaceLeft = this.window.length - this.currentPosition - this.lookahead;
        if (len > spaceLeft) {
            this.slide();
        }
        System.arraycopy(data, off, this.window, this.currentPosition + this.lookahead, len);
        this.lookahead += len;
        if (!this.initialized && this.lookahead >= this.params.getMinBackReferenceLength()) {
            this.initialize();
        }
        if (this.initialized) {
            this.compress();
        }
    }
    
    private void slide() throws IOException {
        final int wSize = this.params.getWindowSize();
        if (this.blockStart != this.currentPosition && this.blockStart < wSize) {
            this.flushLiteralBlock();
            this.blockStart = this.currentPosition;
        }
        System.arraycopy(this.window, wSize, this.window, 0, wSize);
        this.currentPosition -= wSize;
        this.matchStart -= wSize;
        this.blockStart -= wSize;
        for (int i = 0; i < 32768; ++i) {
            final int h = this.head[i];
            this.head[i] = ((h >= wSize) ? (h - wSize) : -1);
        }
        for (int i = 0; i < wSize; ++i) {
            final int p = this.prev[i];
            this.prev[i] = ((p >= wSize) ? (p - wSize) : -1);
        }
    }
    
    private void initialize() {
        for (int i = 0; i < 2; ++i) {
            this.insertHash = this.nextHash(this.insertHash, this.window[i]);
        }
        this.initialized = true;
    }
    
    private void compress() throws IOException {
        final int minMatch = this.params.getMinBackReferenceLength();
        final boolean lazy = this.params.getLazyMatching();
        final int lazyThreshold = this.params.getLazyMatchingThreshold();
        while (this.lookahead >= minMatch) {
            this.catchUpMissedInserts();
            int matchLength = 0;
            final int hashHead = this.insertString(this.currentPosition);
            if (hashHead != -1 && hashHead - this.currentPosition <= this.params.getMaxOffset()) {
                matchLength = this.longestMatch(hashHead);
                if (lazy && matchLength <= lazyThreshold && this.lookahead > minMatch) {
                    matchLength = this.longestMatchForNextPosition(matchLength);
                }
            }
            if (matchLength >= minMatch) {
                if (this.blockStart != this.currentPosition) {
                    this.flushLiteralBlock();
                    this.blockStart = -1;
                }
                this.flushBackReference(matchLength);
                this.insertStringsInMatch(matchLength);
                this.lookahead -= matchLength;
                this.currentPosition += matchLength;
                this.blockStart = this.currentPosition;
            }
            else {
                --this.lookahead;
                ++this.currentPosition;
                if (this.currentPosition - this.blockStart < this.params.getMaxLiteralLength()) {
                    continue;
                }
                this.flushLiteralBlock();
                this.blockStart = this.currentPosition;
            }
        }
    }
    
    private int insertString(final int pos) {
        this.insertHash = this.nextHash(this.insertHash, this.window[pos - 1 + 3]);
        final int hashHead = this.head[this.insertHash];
        this.prev[pos & this.wMask] = hashHead;
        this.head[this.insertHash] = pos;
        return hashHead;
    }
    
    private int longestMatchForNextPosition(final int prevMatchLength) {
        final int prevMatchStart = this.matchStart;
        final int prevInsertHash = this.insertHash;
        --this.lookahead;
        ++this.currentPosition;
        final int hashHead = this.insertString(this.currentPosition);
        final int prevHashHead = this.prev[this.currentPosition & this.wMask];
        int matchLength = this.longestMatch(hashHead);
        if (matchLength <= prevMatchLength) {
            matchLength = prevMatchLength;
            this.matchStart = prevMatchStart;
            this.head[this.insertHash] = prevHashHead;
            this.insertHash = prevInsertHash;
            --this.currentPosition;
            ++this.lookahead;
        }
        return matchLength;
    }
    
    private void insertStringsInMatch(final int matchLength) {
        final int stop = Math.min(matchLength - 1, this.lookahead - 3);
        for (int i = 1; i <= stop; ++i) {
            this.insertString(this.currentPosition + i);
        }
        this.missedInserts = matchLength - stop - 1;
    }
    
    private void catchUpMissedInserts() {
        while (this.missedInserts > 0) {
            this.insertString(this.currentPosition - this.missedInserts--);
        }
    }
    
    private void flushBackReference(final int matchLength) throws IOException {
        this.callback.accept(new BackReference(this.currentPosition - this.matchStart, matchLength));
    }
    
    private void flushLiteralBlock() throws IOException {
        this.callback.accept(new LiteralBlock(this.window, this.blockStart, this.currentPosition - this.blockStart));
    }
    
    private int longestMatch(int matchHead) {
        final int minLength = this.params.getMinBackReferenceLength();
        int longestMatchLength = minLength - 1;
        final int maxPossibleLength = Math.min(this.params.getMaxBackReferenceLength(), this.lookahead);
        final int minIndex = Math.max(0, this.currentPosition - this.params.getMaxOffset());
        final int niceBackReferenceLength = Math.min(maxPossibleLength, this.params.getNiceBackReferenceLength());
        for (int maxCandidates = this.params.getMaxCandidates(), candidates = 0; candidates < maxCandidates && matchHead >= minIndex; matchHead = this.prev[matchHead & this.wMask], ++candidates) {
            int currentLength = 0;
            for (int i = 0; i < maxPossibleLength && this.window[matchHead + i] == this.window[this.currentPosition + i]; ++i) {
                ++currentLength;
            }
            if (currentLength > longestMatchLength) {
                longestMatchLength = currentLength;
                this.matchStart = matchHead;
                if (currentLength >= niceBackReferenceLength) {
                    break;
                }
            }
        }
        return longestMatchLength;
    }
    
    static {
        THE_EOD = new EOD();
    }
    
    public abstract static class Block
    {
        public abstract BlockType getType();
        
        public enum BlockType
        {
            LITERAL, 
            BACK_REFERENCE, 
            EOD;
        }
    }
    
    public static final class LiteralBlock extends Block
    {
        private final byte[] data;
        private final int offset;
        private final int length;
        
        public LiteralBlock(final byte[] data, final int offset, final int length) {
            this.data = data;
            this.offset = offset;
            this.length = length;
        }
        
        public byte[] getData() {
            return this.data;
        }
        
        public int getOffset() {
            return this.offset;
        }
        
        public int getLength() {
            return this.length;
        }
        
        @Override
        public BlockType getType() {
            return BlockType.LITERAL;
        }
        
        @Override
        public String toString() {
            return "LiteralBlock starting at " + this.offset + " with length " + this.length;
        }
    }
    
    public static final class BackReference extends Block
    {
        private final int offset;
        private final int length;
        
        public BackReference(final int offset, final int length) {
            this.offset = offset;
            this.length = length;
        }
        
        public int getOffset() {
            return this.offset;
        }
        
        public int getLength() {
            return this.length;
        }
        
        @Override
        public BlockType getType() {
            return BlockType.BACK_REFERENCE;
        }
        
        @Override
        public String toString() {
            return "BackReference with offset " + this.offset + " and length " + this.length;
        }
    }
    
    public static final class EOD extends Block
    {
        @Override
        public BlockType getType() {
            return BlockType.EOD;
        }
    }
    
    public interface Callback
    {
        void accept(final Block p0) throws IOException;
    }
}
