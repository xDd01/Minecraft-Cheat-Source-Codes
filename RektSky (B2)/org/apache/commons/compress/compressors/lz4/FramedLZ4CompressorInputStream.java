package org.apache.commons.compress.compressors.lz4;

import org.apache.commons.compress.compressors.*;
import java.io.*;
import org.apache.commons.compress.utils.*;
import java.util.zip.*;
import java.util.*;

public class FramedLZ4CompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    static final byte[] LZ4_SIGNATURE;
    private static final byte[] SKIPPABLE_FRAME_TRAILER;
    private static final byte SKIPPABLE_FRAME_PREFIX_BYTE_MASK = 80;
    static final int VERSION_MASK = 192;
    static final int SUPPORTED_VERSION = 64;
    static final int BLOCK_INDEPENDENCE_MASK = 32;
    static final int BLOCK_CHECKSUM_MASK = 16;
    static final int CONTENT_SIZE_MASK = 8;
    static final int CONTENT_CHECKSUM_MASK = 4;
    static final int BLOCK_MAX_SIZE_MASK = 112;
    static final int UNCOMPRESSED_FLAG_MASK = Integer.MIN_VALUE;
    private final byte[] oneByte;
    private final ByteUtils.ByteSupplier supplier;
    private final CountingInputStream in;
    private final boolean decompressConcatenated;
    private boolean expectBlockChecksum;
    private boolean expectBlockDependency;
    private boolean expectContentSize;
    private boolean expectContentChecksum;
    private InputStream currentBlock;
    private boolean endReached;
    private boolean inUncompressed;
    private final XXHash32 contentHash;
    private final XXHash32 blockHash;
    private byte[] blockDependencyBuffer;
    
    public FramedLZ4CompressorInputStream(final InputStream in) throws IOException {
        this(in, false);
    }
    
    public FramedLZ4CompressorInputStream(final InputStream in, final boolean decompressConcatenated) throws IOException {
        this.oneByte = new byte[1];
        this.supplier = new ByteUtils.ByteSupplier() {
            @Override
            public int getAsByte() throws IOException {
                return FramedLZ4CompressorInputStream.this.readOneByte();
            }
        };
        this.contentHash = new XXHash32();
        this.blockHash = new XXHash32();
        this.in = new CountingInputStream(in);
        this.decompressConcatenated = decompressConcatenated;
        this.init(true);
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.oneByte, 0, 1) == -1) ? -1 : (this.oneByte[0] & 0xFF);
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (this.currentBlock != null) {
                this.currentBlock.close();
                this.currentBlock = null;
            }
        }
        finally {
            this.in.close();
        }
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.endReached) {
            return -1;
        }
        int r = this.readOnce(b, off, len);
        if (r == -1) {
            this.nextBlock();
            if (!this.endReached) {
                r = this.readOnce(b, off, len);
            }
        }
        if (r != -1) {
            if (this.expectBlockDependency) {
                this.appendToBlockDependencyBuffer(b, off, r);
            }
            if (this.expectContentChecksum) {
                this.contentHash.update(b, off, r);
            }
        }
        return r;
    }
    
    @Override
    public long getCompressedCount() {
        return this.in.getBytesRead();
    }
    
    private void init(final boolean firstFrame) throws IOException {
        if (this.readSignature(firstFrame)) {
            this.readFrameDescriptor();
            this.nextBlock();
        }
    }
    
    private boolean readSignature(final boolean firstFrame) throws IOException {
        final String garbageMessage = firstFrame ? "Not a LZ4 frame stream" : "LZ4 frame stream followed by garbage";
        final byte[] b = new byte[4];
        int read = IOUtils.readFully(this.in, b);
        this.count(read);
        if (0 == read && !firstFrame) {
            this.endReached = true;
            return false;
        }
        if (4 != read) {
            throw new IOException(garbageMessage);
        }
        read = this.skipSkippableFrame(b);
        if (0 == read && !firstFrame) {
            this.endReached = true;
            return false;
        }
        if (4 != read || !matches(b, 4)) {
            throw new IOException(garbageMessage);
        }
        return true;
    }
    
    private void readFrameDescriptor() throws IOException {
        final int flags = this.readOneByte();
        if (flags == -1) {
            throw new IOException("Premature end of stream while reading frame flags");
        }
        this.contentHash.update(flags);
        if ((flags & 0xC0) != 0x40) {
            throw new IOException("Unsupported version " + (flags >> 6));
        }
        this.expectBlockDependency = ((flags & 0x20) == 0x0);
        if (this.expectBlockDependency) {
            if (this.blockDependencyBuffer == null) {
                this.blockDependencyBuffer = new byte[65536];
            }
        }
        else {
            this.blockDependencyBuffer = null;
        }
        this.expectBlockChecksum = ((flags & 0x10) != 0x0);
        this.expectContentSize = ((flags & 0x8) != 0x0);
        this.expectContentChecksum = ((flags & 0x4) != 0x0);
        final int bdByte = this.readOneByte();
        if (bdByte == -1) {
            throw new IOException("Premature end of stream while reading frame BD byte");
        }
        this.contentHash.update(bdByte);
        if (this.expectContentSize) {
            final byte[] contentSize = new byte[8];
            final int skipped = IOUtils.readFully(this.in, contentSize);
            this.count(skipped);
            if (8 != skipped) {
                throw new IOException("Premature end of stream while reading content size");
            }
            this.contentHash.update(contentSize, 0, contentSize.length);
        }
        final int headerHash = this.readOneByte();
        if (headerHash == -1) {
            throw new IOException("Premature end of stream while reading frame header checksum");
        }
        final int expectedHash = (int)(this.contentHash.getValue() >> 8 & 0xFFL);
        this.contentHash.reset();
        if (headerHash != expectedHash) {
            throw new IOException("frame header checksum mismatch.");
        }
    }
    
    private void nextBlock() throws IOException {
        this.maybeFinishCurrentBlock();
        final long len = ByteUtils.fromLittleEndian(this.supplier, 4);
        final boolean uncompressed = (len & 0xFFFFFFFF80000000L) != 0x0L;
        final int realLen = (int)(len & 0x7FFFFFFFL);
        if (realLen == 0) {
            this.verifyContentChecksum();
            if (!this.decompressConcatenated) {
                this.endReached = true;
            }
            else {
                this.init(false);
            }
            return;
        }
        InputStream capped = new BoundedInputStream(this.in, realLen);
        if (this.expectBlockChecksum) {
            capped = new ChecksumCalculatingInputStream(this.blockHash, capped);
        }
        if (uncompressed) {
            this.inUncompressed = true;
            this.currentBlock = capped;
        }
        else {
            this.inUncompressed = false;
            final BlockLZ4CompressorInputStream s = new BlockLZ4CompressorInputStream(capped);
            if (this.expectBlockDependency) {
                s.prefill(this.blockDependencyBuffer);
            }
            this.currentBlock = s;
        }
    }
    
    private void maybeFinishCurrentBlock() throws IOException {
        if (this.currentBlock != null) {
            this.currentBlock.close();
            this.currentBlock = null;
            if (this.expectBlockChecksum) {
                this.verifyChecksum(this.blockHash, "block");
                this.blockHash.reset();
            }
        }
    }
    
    private void verifyContentChecksum() throws IOException {
        if (this.expectContentChecksum) {
            this.verifyChecksum(this.contentHash, "content");
        }
        this.contentHash.reset();
    }
    
    private void verifyChecksum(final XXHash32 hash, final String kind) throws IOException {
        final byte[] checksum = new byte[4];
        final int read = IOUtils.readFully(this.in, checksum);
        this.count(read);
        if (4 != read) {
            throw new IOException("Premature end of stream while reading " + kind + " checksum");
        }
        final long expectedHash = hash.getValue();
        if (expectedHash != ByteUtils.fromLittleEndian(checksum)) {
            throw new IOException(kind + " checksum mismatch.");
        }
    }
    
    private int readOneByte() throws IOException {
        final int b = this.in.read();
        if (b != -1) {
            this.count(1);
            return b & 0xFF;
        }
        return -1;
    }
    
    private int readOnce(final byte[] b, final int off, final int len) throws IOException {
        if (this.inUncompressed) {
            final int cnt = this.currentBlock.read(b, off, len);
            this.count(cnt);
            return cnt;
        }
        final BlockLZ4CompressorInputStream l = (BlockLZ4CompressorInputStream)this.currentBlock;
        final long before = l.getBytesRead();
        final int cnt2 = this.currentBlock.read(b, off, len);
        this.count(l.getBytesRead() - before);
        return cnt2;
    }
    
    private static boolean isSkippableFrameSignature(final byte[] b) {
        if ((b[0] & 0x50) != 0x50) {
            return false;
        }
        for (int i = 1; i < 4; ++i) {
            if (b[i] != FramedLZ4CompressorInputStream.SKIPPABLE_FRAME_TRAILER[i - 1]) {
                return false;
            }
        }
        return true;
    }
    
    private int skipSkippableFrame(final byte[] b) throws IOException {
        int read = 4;
        while (read == 4 && isSkippableFrameSignature(b)) {
            final long len = ByteUtils.fromLittleEndian(this.supplier, 4);
            final long skipped = IOUtils.skip(this.in, len);
            this.count(skipped);
            if (len != skipped) {
                throw new IOException("Premature end of stream while skipping frame");
            }
            read = IOUtils.readFully(this.in, b);
            this.count(read);
        }
        return read;
    }
    
    private void appendToBlockDependencyBuffer(final byte[] b, final int off, int len) {
        len = Math.min(len, this.blockDependencyBuffer.length);
        if (len > 0) {
            final int keep = this.blockDependencyBuffer.length - len;
            if (keep > 0) {
                System.arraycopy(this.blockDependencyBuffer, len, this.blockDependencyBuffer, 0, keep);
            }
            System.arraycopy(b, off, this.blockDependencyBuffer, keep, len);
        }
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < FramedLZ4CompressorInputStream.LZ4_SIGNATURE.length) {
            return false;
        }
        byte[] shortenedSig = signature;
        if (signature.length > FramedLZ4CompressorInputStream.LZ4_SIGNATURE.length) {
            shortenedSig = new byte[FramedLZ4CompressorInputStream.LZ4_SIGNATURE.length];
            System.arraycopy(signature, 0, shortenedSig, 0, FramedLZ4CompressorInputStream.LZ4_SIGNATURE.length);
        }
        return Arrays.equals(shortenedSig, FramedLZ4CompressorInputStream.LZ4_SIGNATURE);
    }
    
    static {
        LZ4_SIGNATURE = new byte[] { 4, 34, 77, 24 };
        SKIPPABLE_FRAME_TRAILER = new byte[] { 42, 77, 24 };
    }
}
