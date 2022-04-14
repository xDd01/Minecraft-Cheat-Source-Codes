package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.utils.*;
import java.util.zip.*;
import java.io.*;

class InflaterInputStreamWithStatistics extends InflaterInputStream implements InputStreamStatistics
{
    private long compressedCount;
    private long uncompressedCount;
    
    public InflaterInputStreamWithStatistics(final InputStream in) {
        super(in);
        this.compressedCount = 0L;
        this.uncompressedCount = 0L;
    }
    
    public InflaterInputStreamWithStatistics(final InputStream in, final Inflater inf) {
        super(in, inf);
        this.compressedCount = 0L;
        this.uncompressedCount = 0L;
    }
    
    public InflaterInputStreamWithStatistics(final InputStream in, final Inflater inf, final int size) {
        super(in, inf, size);
        this.compressedCount = 0L;
        this.uncompressedCount = 0L;
    }
    
    @Override
    protected void fill() throws IOException {
        super.fill();
        this.compressedCount += this.inf.getRemaining();
    }
    
    @Override
    public int read() throws IOException {
        final int b = super.read();
        if (b > -1) {
            ++this.uncompressedCount;
        }
        return b;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytes = super.read(b, off, len);
        if (bytes > -1) {
            this.uncompressedCount += bytes;
        }
        return bytes;
    }
    
    @Override
    public long getCompressedCount() {
        return this.compressedCount;
    }
    
    @Override
    public long getUncompressedCount() {
        return this.uncompressedCount;
    }
}
