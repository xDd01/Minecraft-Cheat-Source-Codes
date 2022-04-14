package org.apache.commons.compress.compressors.deflate64;

import org.apache.commons.compress.utils.*;
import java.nio.*;
import java.util.*;
import java.io.*;

class HuffmanDecoder implements Closeable
{
    private static final short[] RUN_LENGTH_TABLE;
    private static final int[] DISTANCE_TABLE;
    private static final int[] CODE_LENGTHS_ORDER;
    private static final int[] FIXED_LITERALS;
    private static final int[] FIXED_DISTANCE;
    private boolean finalBlock;
    private DecoderState state;
    private BitInputStream reader;
    private final InputStream in;
    private final DecodingMemory memory;
    
    HuffmanDecoder(final InputStream in) {
        this.finalBlock = false;
        this.memory = new DecodingMemory();
        this.reader = new BitInputStream(in, ByteOrder.LITTLE_ENDIAN);
        this.in = in;
        this.state = new InitialState();
    }
    
    @Override
    public void close() {
        this.state = new InitialState();
        this.reader = null;
    }
    
    public int decode(final byte[] b) throws IOException {
        return this.decode(b, 0, b.length);
    }
    
    public int decode(final byte[] b, final int off, final int len) throws IOException {
        while (!this.finalBlock || this.state.hasData()) {
            if (this.state.state() != HuffmanState.INITIAL) {
                return this.state.read(b, off, len);
            }
            this.finalBlock = (this.readBits(1) == 1L);
            final int mode = (int)this.readBits(2);
            switch (mode) {
                case 0: {
                    this.switchToUncompressedState();
                    continue;
                }
                case 1: {
                    this.state = new HuffmanCodes(HuffmanState.FIXED_CODES, HuffmanDecoder.FIXED_LITERALS, HuffmanDecoder.FIXED_DISTANCE);
                    continue;
                }
                case 2: {
                    final int[][] tables = this.readDynamicTables();
                    this.state = new HuffmanCodes(HuffmanState.DYNAMIC_CODES, tables[0], tables[1]);
                    continue;
                }
                default: {
                    throw new IllegalStateException("Unsupported compression: " + mode);
                }
            }
        }
        return -1;
    }
    
    long getBytesRead() {
        return this.reader.getBytesRead();
    }
    
    private void switchToUncompressedState() throws IOException {
        this.reader.alignWithByteBoundary();
        final long bLen = this.readBits(16);
        final long bNLen = this.readBits(16);
        if (((bLen ^ 0xFFFFL) & 0xFFFFL) != bNLen) {
            throw new IllegalStateException("Illegal LEN / NLEN values");
        }
        this.state = new UncompressedState(bLen);
    }
    
    private int[][] readDynamicTables() throws IOException {
        final int[][] result = new int[2][];
        final int literals = (int)(this.readBits(5) + 257L);
        result[0] = new int[literals];
        final int distances = (int)(this.readBits(5) + 1L);
        result[1] = new int[distances];
        populateDynamicTables(this.reader, result[0], result[1]);
        return result;
    }
    
    int available() throws IOException {
        return this.state.available();
    }
    
    private static int nextSymbol(final BitInputStream reader, final BinaryTreeNode tree) throws IOException {
        BinaryTreeNode node;
        long bit;
        for (node = tree; node != null && node.literal == -1; node = ((bit == 0L) ? node.leftNode : node.rightNode)) {
            bit = readBits(reader, 1);
        }
        return (node != null) ? node.literal : -1;
    }
    
    private static void populateDynamicTables(final BitInputStream reader, final int[] literals, final int[] distances) throws IOException {
        final int codeLengths = (int)(readBits(reader, 4) + 4L);
        final int[] codeLengthValues = new int[19];
        for (int cLen = 0; cLen < codeLengths; ++cLen) {
            codeLengthValues[HuffmanDecoder.CODE_LENGTHS_ORDER[cLen]] = (int)readBits(reader, 3);
        }
        final BinaryTreeNode codeLengthTree = buildTree(codeLengthValues);
        final int[] auxBuffer = new int[literals.length + distances.length];
        int value = -1;
        int length = 0;
        int off = 0;
        while (off < auxBuffer.length) {
            if (length > 0) {
                auxBuffer[off++] = value;
                --length;
            }
            else {
                final int symbol = nextSymbol(reader, codeLengthTree);
                if (symbol < 16) {
                    value = symbol;
                    auxBuffer[off++] = value;
                }
                else if (symbol == 16) {
                    length = (int)(readBits(reader, 2) + 3L);
                }
                else if (symbol == 17) {
                    value = 0;
                    length = (int)(readBits(reader, 3) + 3L);
                }
                else {
                    if (symbol != 18) {
                        continue;
                    }
                    value = 0;
                    length = (int)(readBits(reader, 7) + 11L);
                }
            }
        }
        System.arraycopy(auxBuffer, 0, literals, 0, literals.length);
        System.arraycopy(auxBuffer, literals.length, distances, 0, distances.length);
    }
    
    private static BinaryTreeNode buildTree(final int[] litTable) {
        final int[] literalCodes = getCodes(litTable);
        final BinaryTreeNode root = new BinaryTreeNode(0);
        for (int i = 0; i < litTable.length; ++i) {
            final int len = litTable[i];
            if (len != 0) {
                BinaryTreeNode node = root;
                final int lit = literalCodes[len - 1];
                for (int p = len - 1; p >= 0; --p) {
                    final int bit = lit & 1 << p;
                    node = ((bit == 0) ? node.left() : node.right());
                }
                node.leaf(i);
                final int[] array = literalCodes;
                final int n = len - 1;
                ++array[n];
            }
        }
        return root;
    }
    
    private static int[] getCodes(final int[] litTable) {
        int max = 0;
        int[] blCount = new int[65];
        for (final int aLitTable : litTable) {
            max = Math.max(max, aLitTable);
            final int[] array = blCount;
            final int n = aLitTable;
            ++array[n];
        }
        blCount = Arrays.copyOf(blCount, max + 1);
        int code = 0;
        final int[] nextCode = new int[max + 1];
        for (int i = 0; i <= max; ++i) {
            code = code + blCount[i] << 1;
            nextCode[i] = code;
        }
        return nextCode;
    }
    
    private long readBits(final int numBits) throws IOException {
        return readBits(this.reader, numBits);
    }
    
    private static long readBits(final BitInputStream reader, final int numBits) throws IOException {
        final long r = reader.readBits(numBits);
        if (r == -1L) {
            throw new EOFException("Truncated Deflate64 Stream");
        }
        return r;
    }
    
    static {
        RUN_LENGTH_TABLE = new short[] { 96, 128, 160, 192, 224, 256, 288, 320, 353, 417, 481, 545, 610, 738, 866, 994, 1123, 1379, 1635, 1891, 2148, 2660, 3172, 3684, 4197, 5221, 6245, 7269, 112 };
        DISTANCE_TABLE = new int[] { 16, 32, 48, 64, 81, 113, 146, 210, 275, 403, 532, 788, 1045, 1557, 2070, 3094, 4119, 6167, 8216, 12312, 16409, 24601, 32794, 49178, 65563, 98331, 131100, 196636, 262173, 393245, 524318, 786462 };
        CODE_LENGTHS_ORDER = new int[] { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };
        Arrays.fill(FIXED_LITERALS = new int[288], 0, 144, 8);
        Arrays.fill(HuffmanDecoder.FIXED_LITERALS, 144, 256, 9);
        Arrays.fill(HuffmanDecoder.FIXED_LITERALS, 256, 280, 7);
        Arrays.fill(HuffmanDecoder.FIXED_LITERALS, 280, 288, 8);
        Arrays.fill(FIXED_DISTANCE = new int[32], 5);
    }
    
    private abstract static class DecoderState
    {
        abstract HuffmanState state();
        
        abstract int read(final byte[] p0, final int p1, final int p2) throws IOException;
        
        abstract boolean hasData();
        
        abstract int available() throws IOException;
    }
    
    private class UncompressedState extends DecoderState
    {
        private final long blockLength;
        private long read;
        
        private UncompressedState(final long blockLength) {
            this.blockLength = blockLength;
        }
        
        @Override
        HuffmanState state() {
            return (this.read < this.blockLength) ? HuffmanState.STORED : HuffmanState.INITIAL;
        }
        
        @Override
        int read(final byte[] b, final int off, final int len) throws IOException {
            final int max = (int)Math.min(this.blockLength - this.read, len);
            int readNow;
            for (int readSoFar = 0; readSoFar < max; readSoFar += readNow) {
                if (HuffmanDecoder.this.reader.bitsCached() > 0) {
                    final byte next = (byte)HuffmanDecoder.this.readBits(8);
                    b[off + readSoFar] = HuffmanDecoder.this.memory.add(next);
                    readNow = 1;
                }
                else {
                    readNow = HuffmanDecoder.this.in.read(b, off + readSoFar, max - readSoFar);
                    if (readNow == -1) {
                        throw new EOFException("Truncated Deflate64 Stream");
                    }
                    HuffmanDecoder.this.memory.add(b, off + readSoFar, readNow);
                }
                this.read += readNow;
            }
            return max;
        }
        
        @Override
        boolean hasData() {
            return this.read < this.blockLength;
        }
        
        @Override
        int available() throws IOException {
            return (int)Math.min(this.blockLength - this.read, HuffmanDecoder.this.reader.bitsAvailable() / 8L);
        }
    }
    
    private class InitialState extends DecoderState
    {
        @Override
        HuffmanState state() {
            return HuffmanState.INITIAL;
        }
        
        @Override
        int read(final byte[] b, final int off, final int len) throws IOException {
            throw new IllegalStateException("Cannot read in this state");
        }
        
        @Override
        boolean hasData() {
            return false;
        }
        
        @Override
        int available() {
            return 0;
        }
    }
    
    private class HuffmanCodes extends DecoderState
    {
        private boolean endOfBlock;
        private final HuffmanState state;
        private final BinaryTreeNode lengthTree;
        private final BinaryTreeNode distanceTree;
        private int runBufferPos;
        private byte[] runBuffer;
        private int runBufferLength;
        
        HuffmanCodes(final HuffmanState state, final int[] lengths, final int[] distance) {
            this.endOfBlock = false;
            this.runBufferPos = 0;
            this.runBuffer = new byte[0];
            this.runBufferLength = 0;
            this.state = state;
            this.lengthTree = buildTree(lengths);
            this.distanceTree = buildTree(distance);
        }
        
        @Override
        HuffmanState state() {
            return this.endOfBlock ? HuffmanState.INITIAL : this.state;
        }
        
        @Override
        int read(final byte[] b, final int off, final int len) throws IOException {
            return this.decodeNext(b, off, len);
        }
        
        private int decodeNext(final byte[] b, final int off, final int len) throws IOException {
            if (this.endOfBlock) {
                return -1;
            }
            int result = this.copyFromRunBuffer(b, off, len);
            while (result < len) {
                final int symbol = nextSymbol(HuffmanDecoder.this.reader, this.lengthTree);
                if (symbol < 256) {
                    b[off + result++] = HuffmanDecoder.this.memory.add((byte)symbol);
                }
                else {
                    if (symbol <= 256) {
                        this.endOfBlock = true;
                        return result;
                    }
                    final int runMask = HuffmanDecoder.RUN_LENGTH_TABLE[symbol - 257];
                    int run = runMask >>> 5;
                    final int runXtra = runMask & 0x1F;
                    run += (int)HuffmanDecoder.this.readBits(runXtra);
                    final int distSym = nextSymbol(HuffmanDecoder.this.reader, this.distanceTree);
                    final int distMask = HuffmanDecoder.DISTANCE_TABLE[distSym];
                    int dist = distMask >>> 4;
                    final int distXtra = distMask & 0xF;
                    dist += (int)HuffmanDecoder.this.readBits(distXtra);
                    if (this.runBuffer.length < run) {
                        this.runBuffer = new byte[run];
                    }
                    this.runBufferLength = run;
                    this.runBufferPos = 0;
                    HuffmanDecoder.this.memory.recordToBuffer(dist, run, this.runBuffer);
                    result += this.copyFromRunBuffer(b, off + result, len - result);
                }
            }
            return result;
        }
        
        private int copyFromRunBuffer(final byte[] b, final int off, final int len) {
            final int bytesInBuffer = this.runBufferLength - this.runBufferPos;
            int copiedBytes = 0;
            if (bytesInBuffer > 0) {
                copiedBytes = Math.min(len, bytesInBuffer);
                System.arraycopy(this.runBuffer, this.runBufferPos, b, off, copiedBytes);
                this.runBufferPos += copiedBytes;
            }
            return copiedBytes;
        }
        
        @Override
        boolean hasData() {
            return !this.endOfBlock;
        }
        
        @Override
        int available() {
            return this.runBufferLength - this.runBufferPos;
        }
    }
    
    private static class BinaryTreeNode
    {
        private final int bits;
        int literal;
        BinaryTreeNode leftNode;
        BinaryTreeNode rightNode;
        
        private BinaryTreeNode(final int bits) {
            this.literal = -1;
            this.bits = bits;
        }
        
        void leaf(final int symbol) {
            this.literal = symbol;
            this.leftNode = null;
            this.rightNode = null;
        }
        
        BinaryTreeNode left() {
            if (this.leftNode == null && this.literal == -1) {
                this.leftNode = new BinaryTreeNode(this.bits + 1);
            }
            return this.leftNode;
        }
        
        BinaryTreeNode right() {
            if (this.rightNode == null && this.literal == -1) {
                this.rightNode = new BinaryTreeNode(this.bits + 1);
            }
            return this.rightNode;
        }
    }
    
    private static class DecodingMemory
    {
        private final byte[] memory;
        private final int mask;
        private int wHead;
        private boolean wrappedAround;
        
        private DecodingMemory() {
            this(16);
        }
        
        private DecodingMemory(final int bits) {
            this.memory = new byte[1 << bits];
            this.mask = this.memory.length - 1;
        }
        
        byte add(final byte b) {
            this.memory[this.wHead] = b;
            this.wHead = this.incCounter(this.wHead);
            return b;
        }
        
        void add(final byte[] b, final int off, final int len) {
            for (int i = off; i < off + len; ++i) {
                this.add(b[i]);
            }
        }
        
        void recordToBuffer(final int distance, final int length, final byte[] buff) {
            if (distance > this.memory.length) {
                throw new IllegalStateException("Illegal distance parameter: " + distance);
            }
            final int start = this.wHead - distance & this.mask;
            if (!this.wrappedAround && start >= this.wHead) {
                throw new IllegalStateException("Attempt to read beyond memory: dist=" + distance);
            }
            for (int i = 0, pos = start; i < length; ++i, pos = this.incCounter(pos)) {
                buff[i] = this.add(this.memory[pos]);
            }
        }
        
        private int incCounter(final int counter) {
            final int newCounter = counter + 1 & this.mask;
            if (!this.wrappedAround && newCounter < counter) {
                this.wrappedAround = true;
            }
            return newCounter;
        }
    }
}
