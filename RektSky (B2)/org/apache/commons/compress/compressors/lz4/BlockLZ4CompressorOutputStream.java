package org.apache.commons.compress.compressors.lz4;

import org.apache.commons.compress.compressors.*;
import java.io.*;
import org.apache.commons.compress.compressors.lz77support.*;
import org.apache.commons.compress.utils.*;
import java.util.*;

public class BlockLZ4CompressorOutputStream extends CompressorOutputStream
{
    private static final int MIN_BACK_REFERENCE_LENGTH = 4;
    private static final int MIN_OFFSET_OF_LAST_BACK_REFERENCE = 12;
    private final LZ77Compressor compressor;
    private final OutputStream os;
    private final byte[] oneByte;
    private boolean finished;
    private Deque<Pair> pairs;
    private Deque<byte[]> expandedBlocks;
    
    public BlockLZ4CompressorOutputStream(final OutputStream os) throws IOException {
        this(os, createParameterBuilder().build());
    }
    
    public BlockLZ4CompressorOutputStream(final OutputStream os, final Parameters params) throws IOException {
        this.oneByte = new byte[1];
        this.finished = false;
        this.pairs = new LinkedList<Pair>();
        this.expandedBlocks = new LinkedList<byte[]>();
        this.os = os;
        this.compressor = new LZ77Compressor(params, new LZ77Compressor.Callback() {
            @Override
            public void accept(final LZ77Compressor.Block block) throws IOException {
                switch (block.getType()) {
                    case LITERAL: {
                        BlockLZ4CompressorOutputStream.this.addLiteralBlock((LZ77Compressor.LiteralBlock)block);
                        break;
                    }
                    case BACK_REFERENCE: {
                        BlockLZ4CompressorOutputStream.this.addBackReference((LZ77Compressor.BackReference)block);
                        break;
                    }
                    case EOD: {
                        BlockLZ4CompressorOutputStream.this.writeFinalLiteralBlock();
                        break;
                    }
                }
            }
        });
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.oneByte[0] = (byte)(b & 0xFF);
        this.write(this.oneByte);
    }
    
    @Override
    public void write(final byte[] data, final int off, final int len) throws IOException {
        this.compressor.compress(data, off, len);
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.finish();
        }
        finally {
            this.os.close();
        }
    }
    
    public void finish() throws IOException {
        if (!this.finished) {
            this.compressor.finish();
            this.finished = true;
        }
    }
    
    public void prefill(final byte[] data, final int off, final int len) {
        if (len > 0) {
            final byte[] b = Arrays.copyOfRange(data, off, off + len);
            this.compressor.prefill(b);
            this.recordLiteral(b);
        }
    }
    
    private void addLiteralBlock(final LZ77Compressor.LiteralBlock block) throws IOException {
        final Pair last = this.writeBlocksAndReturnUnfinishedPair(block.getLength());
        this.recordLiteral(last.addLiteral(block));
        this.clearUnusedBlocksAndPairs();
    }
    
    private void addBackReference(final LZ77Compressor.BackReference block) throws IOException {
        final Pair last = this.writeBlocksAndReturnUnfinishedPair(block.getLength());
        last.setBackReference(block);
        this.recordBackReference(block);
        this.clearUnusedBlocksAndPairs();
    }
    
    private Pair writeBlocksAndReturnUnfinishedPair(final int length) throws IOException {
        this.writeWritablePairs(length);
        Pair last = this.pairs.peekLast();
        if (last == null || last.hasBackReference()) {
            last = new Pair();
            this.pairs.addLast(last);
        }
        return last;
    }
    
    private void recordLiteral(final byte[] b) {
        this.expandedBlocks.addFirst(b);
    }
    
    private void clearUnusedBlocksAndPairs() {
        this.clearUnusedBlocks();
        this.clearUnusedPairs();
    }
    
    private void clearUnusedBlocks() {
        int blockLengths = 0;
        int blocksToKeep = 0;
        for (final byte[] b : this.expandedBlocks) {
            ++blocksToKeep;
            blockLengths += b.length;
            if (blockLengths >= 65536) {
                break;
            }
        }
        for (int size = this.expandedBlocks.size(), i = blocksToKeep; i < size; ++i) {
            this.expandedBlocks.removeLast();
        }
    }
    
    private void recordBackReference(final LZ77Compressor.BackReference block) {
        this.expandedBlocks.addFirst(this.expand(block.getOffset(), block.getLength()));
    }
    
    private byte[] expand(final int offset, final int length) {
        final byte[] expanded = new byte[length];
        if (offset == 1) {
            final byte[] block = this.expandedBlocks.peekFirst();
            final byte b = block[block.length - 1];
            if (b != 0) {
                Arrays.fill(expanded, b);
            }
        }
        else {
            this.expandFromList(expanded, offset, length);
        }
        return expanded;
    }
    
    private void expandFromList(final byte[] expanded, final int offset, final int length) {
        int offsetRemaining = offset;
        int copyLen;
        for (int lengthRemaining = length, writeOffset = 0; lengthRemaining > 0; lengthRemaining -= copyLen, writeOffset += copyLen) {
            byte[] block = null;
            int copyOffset;
            if (offsetRemaining > 0) {
                int blockOffset = 0;
                for (final byte[] b : this.expandedBlocks) {
                    if (b.length + blockOffset >= offsetRemaining) {
                        block = b;
                        break;
                    }
                    blockOffset += b.length;
                }
                if (block == null) {
                    throw new IllegalStateException("failed to find a block containing offset " + offset);
                }
                copyOffset = blockOffset + block.length - offsetRemaining;
                copyLen = Math.min(lengthRemaining, block.length - copyOffset);
            }
            else {
                block = expanded;
                copyOffset = -offsetRemaining;
                copyLen = Math.min(lengthRemaining, writeOffset + offsetRemaining);
            }
            System.arraycopy(block, copyOffset, expanded, writeOffset, copyLen);
            offsetRemaining -= copyLen;
        }
    }
    
    private void clearUnusedPairs() {
        int pairLengths = 0;
        int pairsToKeep = 0;
        final Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            final Pair p = it.next();
            ++pairsToKeep;
            pairLengths += p.length();
            if (pairLengths >= 65536) {
                break;
            }
        }
        for (int size = this.pairs.size(), i = pairsToKeep; i < size; ++i) {
            final Pair p2 = this.pairs.peekFirst();
            if (!p2.hasBeenWritten()) {
                break;
            }
            this.pairs.removeFirst();
        }
    }
    
    private void writeFinalLiteralBlock() throws IOException {
        this.rewriteLastPairs();
        for (final Pair p : this.pairs) {
            if (!p.hasBeenWritten()) {
                p.writeTo(this.os);
            }
        }
        this.pairs.clear();
    }
    
    private void writeWritablePairs(final int lengthOfBlocksAfterLastPair) throws IOException {
        int unwrittenLength = lengthOfBlocksAfterLastPair;
        final Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            final Pair p = it.next();
            if (p.hasBeenWritten()) {
                break;
            }
            unwrittenLength += p.length();
        }
        final Iterator<Pair> iterator = this.pairs.iterator();
        while (iterator.hasNext()) {
            final Pair p = iterator.next();
            if (p.hasBeenWritten()) {
                continue;
            }
            unwrittenLength -= p.length();
            if (!p.canBeWritten(unwrittenLength)) {
                break;
            }
            p.writeTo(this.os);
        }
    }
    
    private void rewriteLastPairs() {
        final LinkedList<Pair> lastPairs = new LinkedList<Pair>();
        final LinkedList<Integer> pairLength = new LinkedList<Integer>();
        int offset = 0;
        final Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            final Pair p = it.next();
            if (p.hasBeenWritten()) {
                break;
            }
            final int len = p.length();
            pairLength.addFirst(len);
            lastPairs.addFirst(p);
            offset += len;
            if (offset >= 12) {
                break;
            }
        }
        final Iterator<Pair> iterator = lastPairs.iterator();
        while (iterator.hasNext()) {
            final Pair p = iterator.next();
            this.pairs.remove(p);
        }
        final int lastPairsSize = lastPairs.size();
        int toExpand = 0;
        for (int i = 1; i < lastPairsSize; ++i) {
            toExpand += pairLength.get(i);
        }
        final Pair replacement = new Pair();
        if (toExpand > 0) {
            replacement.prependLiteral(this.expand(toExpand, toExpand));
        }
        final Pair splitCandidate = lastPairs.get(0);
        final int stillNeeded = 12 - toExpand;
        final int brLen = splitCandidate.hasBackReference() ? splitCandidate.backReferenceLength() : 0;
        if (splitCandidate.hasBackReference() && brLen >= 4 + stillNeeded) {
            replacement.prependLiteral(this.expand(toExpand + stillNeeded, stillNeeded));
            this.pairs.add(splitCandidate.splitWithNewBackReferenceLengthOf(brLen - stillNeeded));
        }
        else {
            if (splitCandidate.hasBackReference()) {
                replacement.prependLiteral(this.expand(toExpand + brLen, brLen));
            }
            splitCandidate.prependTo(replacement);
        }
        this.pairs.add(replacement);
    }
    
    public static Parameters.Builder createParameterBuilder() {
        final int maxLen = 65535;
        return Parameters.builder(65536).withMinBackReferenceLength(4).withMaxBackReferenceLength(maxLen).withMaxOffset(maxLen).withMaxLiteralLength(maxLen);
    }
    
    static final class Pair
    {
        private final Deque<byte[]> literals;
        private int brOffset;
        private int brLength;
        private boolean written;
        
        Pair() {
            this.literals = new LinkedList<byte[]>();
        }
        
        private void prependLiteral(final byte[] data) {
            this.literals.addFirst(data);
        }
        
        byte[] addLiteral(final LZ77Compressor.LiteralBlock block) {
            final byte[] copy = Arrays.copyOfRange(block.getData(), block.getOffset(), block.getOffset() + block.getLength());
            this.literals.add(copy);
            return copy;
        }
        
        void setBackReference(final LZ77Compressor.BackReference block) {
            if (this.hasBackReference()) {
                throw new IllegalStateException();
            }
            this.brOffset = block.getOffset();
            this.brLength = block.getLength();
        }
        
        boolean hasBackReference() {
            return this.brOffset > 0;
        }
        
        boolean canBeWritten(final int lengthOfBlocksAfterThisPair) {
            return this.hasBackReference() && lengthOfBlocksAfterThisPair >= 16;
        }
        
        int length() {
            return this.literalLength() + this.brLength;
        }
        
        private boolean hasBeenWritten() {
            return this.written;
        }
        
        void writeTo(final OutputStream out) throws IOException {
            final int litLength = this.literalLength();
            out.write(lengths(litLength, this.brLength));
            if (litLength >= 15) {
                writeLength(litLength - 15, out);
            }
            for (final byte[] b : this.literals) {
                out.write(b);
            }
            if (this.hasBackReference()) {
                ByteUtils.toLittleEndian(out, this.brOffset, 2);
                if (this.brLength - 4 >= 15) {
                    writeLength(this.brLength - 4 - 15, out);
                }
            }
            this.written = true;
        }
        
        private int literalLength() {
            int length = 0;
            for (final byte[] b : this.literals) {
                length += b.length;
            }
            return length;
        }
        
        private static int lengths(final int litLength, final int brLength) {
            final int l = (litLength < 15) ? litLength : 15;
            final int br = (brLength < 4) ? 0 : ((brLength < 19) ? (brLength - 4) : 15);
            return l << 4 | br;
        }
        
        private static void writeLength(int length, final OutputStream out) throws IOException {
            while (length >= 255) {
                out.write(255);
                length -= 255;
            }
            out.write(length);
        }
        
        private int backReferenceLength() {
            return this.brLength;
        }
        
        private void prependTo(final Pair other) {
            final Iterator<byte[]> listBackwards = this.literals.descendingIterator();
            while (listBackwards.hasNext()) {
                other.prependLiteral(listBackwards.next());
            }
        }
        
        private Pair splitWithNewBackReferenceLengthOf(final int newBackReferenceLength) {
            final Pair p = new Pair();
            p.literals.addAll((Collection<?>)this.literals);
            p.brOffset = this.brOffset;
            p.brLength = newBackReferenceLength;
            return p;
        }
    }
}
