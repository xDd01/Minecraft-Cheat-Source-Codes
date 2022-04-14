package org.apache.commons.compress.compressors.lzma;

import org.apache.commons.compress.compressors.*;
import org.tukaani.xz.*;
import java.io.*;
import org.apache.commons.compress.*;
import org.apache.commons.compress.utils.*;

public class LZMACompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private final CountingInputStream countingStream;
    private final InputStream in;
    
    public LZMACompressorInputStream(final InputStream inputStream) throws IOException {
        this.in = (InputStream)new LZMAInputStream((InputStream)(this.countingStream = new CountingInputStream(inputStream)), -1);
    }
    
    public LZMACompressorInputStream(final InputStream inputStream, final int memoryLimitInKb) throws IOException {
        try {
            final CountingInputStream countingStream = new CountingInputStream(inputStream);
            this.countingStream = countingStream;
            this.in = (InputStream)new LZMAInputStream((InputStream)countingStream, memoryLimitInKb);
        }
        catch (org.tukaani.xz.MemoryLimitException e) {
            throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), (Exception)e);
        }
    }
    
    @Override
    public int read() throws IOException {
        final int ret = this.in.read();
        this.count((ret != -1) ? 1 : 0);
        return ret;
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws IOException {
        final int ret = this.in.read(buf, off, len);
        this.count(ret);
        return ret;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return IOUtils.skip(this.in, n);
    }
    
    @Override
    public int available() throws IOException {
        return this.in.available();
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return signature != null && length >= 3 && signature[0] == 93 && signature[1] == 0 && signature[2] == 0;
    }
}
