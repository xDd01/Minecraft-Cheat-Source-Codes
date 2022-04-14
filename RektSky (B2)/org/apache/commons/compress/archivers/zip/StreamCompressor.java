package org.apache.commons.compress.archivers.zip;

import java.util.zip.*;
import java.nio.channels.*;
import org.apache.commons.compress.parallel.*;
import java.io.*;
import java.nio.*;

public abstract class StreamCompressor implements Closeable
{
    private static final int DEFLATER_BLOCK_SIZE = 8192;
    private final Deflater def;
    private final CRC32 crc;
    private long writtenToOutputStreamForLastEntry;
    private long sourcePayloadLength;
    private long totalWrittenToOutputStream;
    private static final int BUFFER_SIZE = 4096;
    private final byte[] outputBuffer;
    private final byte[] readerBuf;
    
    StreamCompressor(final Deflater deflater) {
        this.crc = new CRC32();
        this.writtenToOutputStreamForLastEntry = 0L;
        this.sourcePayloadLength = 0L;
        this.totalWrittenToOutputStream = 0L;
        this.outputBuffer = new byte[4096];
        this.readerBuf = new byte[4096];
        this.def = deflater;
    }
    
    static StreamCompressor create(final OutputStream os, final Deflater deflater) {
        return new OutputStreamCompressor(deflater, os);
    }
    
    static StreamCompressor create(final OutputStream os) {
        return create(os, new Deflater(-1, true));
    }
    
    static StreamCompressor create(final DataOutput os, final Deflater deflater) {
        return new DataOutputCompressor(deflater, os);
    }
    
    static StreamCompressor create(final SeekableByteChannel os, final Deflater deflater) {
        return new SeekableByteChannelCompressor(deflater, os);
    }
    
    public static StreamCompressor create(final int compressionLevel, final ScatterGatherBackingStore bs) {
        final Deflater deflater = new Deflater(compressionLevel, true);
        return new ScatterGatherBackingStoreCompressor(deflater, bs);
    }
    
    public static StreamCompressor create(final ScatterGatherBackingStore bs) {
        return create(-1, bs);
    }
    
    public long getCrc32() {
        return this.crc.getValue();
    }
    
    public long getBytesRead() {
        return this.sourcePayloadLength;
    }
    
    public long getBytesWrittenForLastEntry() {
        return this.writtenToOutputStreamForLastEntry;
    }
    
    public long getTotalBytesWritten() {
        return this.totalWrittenToOutputStream;
    }
    
    public void deflate(final InputStream source, final int method) throws IOException {
        this.reset();
        int length;
        while ((length = source.read(this.readerBuf, 0, this.readerBuf.length)) >= 0) {
            this.write(this.readerBuf, 0, length, method);
        }
        if (method == 8) {
            this.flushDeflater();
        }
    }
    
    long write(final byte[] b, final int offset, final int length, final int method) throws IOException {
        final long current = this.writtenToOutputStreamForLastEntry;
        this.crc.update(b, offset, length);
        if (method == 8) {
            this.writeDeflated(b, offset, length);
        }
        else {
            this.writeCounted(b, offset, length);
        }
        this.sourcePayloadLength += length;
        return this.writtenToOutputStreamForLastEntry - current;
    }
    
    void reset() {
        this.crc.reset();
        this.def.reset();
        this.sourcePayloadLength = 0L;
        this.writtenToOutputStreamForLastEntry = 0L;
    }
    
    @Override
    public void close() throws IOException {
        this.def.end();
    }
    
    void flushDeflater() throws IOException {
        this.def.finish();
        while (!this.def.finished()) {
            this.deflate();
        }
    }
    
    private void writeDeflated(final byte[] b, final int offset, final int length) throws IOException {
        if (length > 0 && !this.def.finished()) {
            if (length <= 8192) {
                this.def.setInput(b, offset, length);
                this.deflateUntilInputIsNeeded();
            }
            else {
                final int fullblocks = length / 8192;
                for (int i = 0; i < fullblocks; ++i) {
                    this.def.setInput(b, offset + i * 8192, 8192);
                    this.deflateUntilInputIsNeeded();
                }
                final int done = fullblocks * 8192;
                if (done < length) {
                    this.def.setInput(b, offset + done, length - done);
                    this.deflateUntilInputIsNeeded();
                }
            }
        }
    }
    
    private void deflateUntilInputIsNeeded() throws IOException {
        while (!this.def.needsInput()) {
            this.deflate();
        }
    }
    
    void deflate() throws IOException {
        final int len = this.def.deflate(this.outputBuffer, 0, this.outputBuffer.length);
        if (len > 0) {
            this.writeCounted(this.outputBuffer, 0, len);
        }
    }
    
    public void writeCounted(final byte[] data) throws IOException {
        this.writeCounted(data, 0, data.length);
    }
    
    public void writeCounted(final byte[] data, final int offset, final int length) throws IOException {
        this.writeOut(data, offset, length);
        this.writtenToOutputStreamForLastEntry += length;
        this.totalWrittenToOutputStream += length;
    }
    
    protected abstract void writeOut(final byte[] p0, final int p1, final int p2) throws IOException;
    
    private static final class ScatterGatherBackingStoreCompressor extends StreamCompressor
    {
        private final ScatterGatherBackingStore bs;
        
        public ScatterGatherBackingStoreCompressor(final Deflater deflater, final ScatterGatherBackingStore bs) {
            super(deflater);
            this.bs = bs;
        }
        
        @Override
        protected final void writeOut(final byte[] data, final int offset, final int length) throws IOException {
            this.bs.writeOut(data, offset, length);
        }
    }
    
    private static final class OutputStreamCompressor extends StreamCompressor
    {
        private final OutputStream os;
        
        public OutputStreamCompressor(final Deflater deflater, final OutputStream os) {
            super(deflater);
            this.os = os;
        }
        
        @Override
        protected final void writeOut(final byte[] data, final int offset, final int length) throws IOException {
            this.os.write(data, offset, length);
        }
    }
    
    private static final class DataOutputCompressor extends StreamCompressor
    {
        private final DataOutput raf;
        
        public DataOutputCompressor(final Deflater deflater, final DataOutput raf) {
            super(deflater);
            this.raf = raf;
        }
        
        @Override
        protected final void writeOut(final byte[] data, final int offset, final int length) throws IOException {
            this.raf.write(data, offset, length);
        }
    }
    
    private static final class SeekableByteChannelCompressor extends StreamCompressor
    {
        private final SeekableByteChannel channel;
        
        public SeekableByteChannelCompressor(final Deflater deflater, final SeekableByteChannel channel) {
            super(deflater);
            this.channel = channel;
        }
        
        @Override
        protected final void writeOut(final byte[] data, final int offset, final int length) throws IOException {
            this.channel.write(ByteBuffer.wrap(data, offset, length));
        }
    }
}
