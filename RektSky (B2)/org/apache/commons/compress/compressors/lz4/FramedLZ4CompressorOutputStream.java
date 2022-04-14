package org.apache.commons.compress.compressors.lz4;

import org.apache.commons.compress.compressors.*;
import java.io.*;
import org.apache.commons.compress.utils.*;
import org.apache.commons.compress.compressors.lz77support.*;

public class FramedLZ4CompressorOutputStream extends CompressorOutputStream
{
    private static final byte[] END_MARK;
    private final byte[] oneByte;
    private final byte[] blockData;
    private final OutputStream out;
    private final Parameters params;
    private boolean finished;
    private int currentIndex;
    private final XXHash32 contentHash;
    private final XXHash32 blockHash;
    private byte[] blockDependencyBuffer;
    private int collectedBlockDependencyBytes;
    
    public FramedLZ4CompressorOutputStream(final OutputStream out) throws IOException {
        this(out, Parameters.DEFAULT);
    }
    
    public FramedLZ4CompressorOutputStream(final OutputStream out, final Parameters params) throws IOException {
        this.oneByte = new byte[1];
        this.finished = false;
        this.currentIndex = 0;
        this.contentHash = new XXHash32();
        this.params = params;
        this.blockData = new byte[params.blockSize.getSize()];
        this.out = out;
        this.blockHash = (params.withBlockChecksum ? new XXHash32() : null);
        out.write(FramedLZ4CompressorInputStream.LZ4_SIGNATURE);
        this.writeFrameDescriptor();
        this.blockDependencyBuffer = (byte[])(params.withBlockDependency ? new byte[65536] : null);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.oneByte[0] = (byte)(b & 0xFF);
        this.write(this.oneByte);
    }
    
    @Override
    public void write(final byte[] data, int off, int len) throws IOException {
        if (this.params.withContentChecksum) {
            this.contentHash.update(data, off, len);
        }
        if (this.currentIndex + len > this.blockData.length) {
            this.flushBlock();
            while (len > this.blockData.length) {
                System.arraycopy(data, off, this.blockData, 0, this.blockData.length);
                off += this.blockData.length;
                len -= this.blockData.length;
                this.currentIndex = this.blockData.length;
                this.flushBlock();
            }
        }
        System.arraycopy(data, off, this.blockData, this.currentIndex, len);
        this.currentIndex += len;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.finish();
        }
        finally {
            this.out.close();
        }
    }
    
    public void finish() throws IOException {
        if (!this.finished) {
            if (this.currentIndex > 0) {
                this.flushBlock();
            }
            this.writeTrailer();
            this.finished = true;
        }
    }
    
    private void writeFrameDescriptor() throws IOException {
        int flags = 64;
        if (!this.params.withBlockDependency) {
            flags |= 0x20;
        }
        if (this.params.withContentChecksum) {
            flags |= 0x4;
        }
        if (this.params.withBlockChecksum) {
            flags |= 0x10;
        }
        this.out.write(flags);
        this.contentHash.update(flags);
        final int bd = this.params.blockSize.getIndex() << 4 & 0x70;
        this.out.write(bd);
        this.contentHash.update(bd);
        this.out.write((int)(this.contentHash.getValue() >> 8 & 0xFFL));
        this.contentHash.reset();
    }
    
    private void flushBlock() throws IOException {
        final boolean withBlockDependency = this.params.withBlockDependency;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final BlockLZ4CompressorOutputStream o = new BlockLZ4CompressorOutputStream(baos, this.params.lz77params)) {
            if (withBlockDependency) {
                o.prefill(this.blockDependencyBuffer, this.blockDependencyBuffer.length - this.collectedBlockDependencyBytes, this.collectedBlockDependencyBytes);
            }
            o.write(this.blockData, 0, this.currentIndex);
        }
        if (withBlockDependency) {
            this.appendToBlockDependencyBuffer(this.blockData, 0, this.currentIndex);
        }
        final byte[] b = baos.toByteArray();
        if (b.length > this.currentIndex) {
            ByteUtils.toLittleEndian(this.out, this.currentIndex | Integer.MIN_VALUE, 4);
            this.out.write(this.blockData, 0, this.currentIndex);
            if (this.params.withBlockChecksum) {
                this.blockHash.update(this.blockData, 0, this.currentIndex);
            }
        }
        else {
            ByteUtils.toLittleEndian(this.out, b.length, 4);
            this.out.write(b);
            if (this.params.withBlockChecksum) {
                this.blockHash.update(b, 0, b.length);
            }
        }
        if (this.params.withBlockChecksum) {
            ByteUtils.toLittleEndian(this.out, this.blockHash.getValue(), 4);
            this.blockHash.reset();
        }
        this.currentIndex = 0;
    }
    
    private void writeTrailer() throws IOException {
        this.out.write(FramedLZ4CompressorOutputStream.END_MARK);
        if (this.params.withContentChecksum) {
            ByteUtils.toLittleEndian(this.out, this.contentHash.getValue(), 4);
        }
    }
    
    private void appendToBlockDependencyBuffer(final byte[] b, final int off, int len) {
        len = Math.min(len, this.blockDependencyBuffer.length);
        if (len > 0) {
            final int keep = this.blockDependencyBuffer.length - len;
            if (keep > 0) {
                System.arraycopy(this.blockDependencyBuffer, len, this.blockDependencyBuffer, 0, keep);
            }
            System.arraycopy(b, off, this.blockDependencyBuffer, keep, len);
            this.collectedBlockDependencyBytes = Math.min(this.collectedBlockDependencyBytes + len, this.blockDependencyBuffer.length);
        }
    }
    
    static {
        END_MARK = new byte[4];
    }
    
    public enum BlockSize
    {
        K64(65536, 4), 
        K256(262144, 5), 
        M1(1048576, 6), 
        M4(4194304, 7);
        
        private final int size;
        private final int index;
        
        private BlockSize(final int size, final int index) {
            this.size = size;
            this.index = index;
        }
        
        int getSize() {
            return this.size;
        }
        
        int getIndex() {
            return this.index;
        }
    }
    
    public static class Parameters
    {
        private final BlockSize blockSize;
        private final boolean withContentChecksum;
        private final boolean withBlockChecksum;
        private final boolean withBlockDependency;
        private final org.apache.commons.compress.compressors.lz77support.Parameters lz77params;
        public static final Parameters DEFAULT;
        
        public Parameters(final BlockSize blockSize) {
            this(blockSize, true, false, false);
        }
        
        public Parameters(final BlockSize blockSize, final org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
            this(blockSize, true, false, false, lz77params);
        }
        
        public Parameters(final BlockSize blockSize, final boolean withContentChecksum, final boolean withBlockChecksum, final boolean withBlockDependency) {
            this(blockSize, withContentChecksum, withBlockChecksum, withBlockDependency, BlockLZ4CompressorOutputStream.createParameterBuilder().build());
        }
        
        public Parameters(final BlockSize blockSize, final boolean withContentChecksum, final boolean withBlockChecksum, final boolean withBlockDependency, final org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
            this.blockSize = blockSize;
            this.withContentChecksum = withContentChecksum;
            this.withBlockChecksum = withBlockChecksum;
            this.withBlockDependency = withBlockDependency;
            this.lz77params = lz77params;
        }
        
        @Override
        public String toString() {
            return "LZ4 Parameters with BlockSize " + this.blockSize + ", withContentChecksum " + this.withContentChecksum + ", withBlockChecksum " + this.withBlockChecksum + ", withBlockDependency " + this.withBlockDependency;
        }
        
        static {
            DEFAULT = new Parameters(BlockSize.M4, true, false, false);
        }
    }
}
