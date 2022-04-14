package org.apache.commons.compress.compressors.zstandard;

import org.apache.commons.compress.compressors.*;
import com.github.luben.zstd.*;
import java.io.*;
import org.apache.commons.compress.utils.*;

public class ZstdCompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private final CountingInputStream countingStream;
    private final ZstdInputStream decIS;
    
    public ZstdCompressorInputStream(final InputStream in) throws IOException {
        this.decIS = new ZstdInputStream((InputStream)(this.countingStream = new CountingInputStream(in)));
    }
    
    @Override
    public int available() throws IOException {
        return this.decIS.available();
    }
    
    @Override
    public void close() throws IOException {
        this.decIS.close();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.decIS.read(b);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return IOUtils.skip((InputStream)this.decIS, n);
    }
    
    @Override
    public void mark(final int readlimit) {
        this.decIS.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.decIS.markSupported();
    }
    
    @Override
    public int read() throws IOException {
        final int ret = this.decIS.read();
        this.count((ret != -1) ? 1 : 0);
        return ret;
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws IOException {
        final int ret = this.decIS.read(buf, off, len);
        this.count(ret);
        return ret;
    }
    
    @Override
    public String toString() {
        return this.decIS.toString();
    }
    
    @Override
    public void reset() throws IOException {
        this.decIS.reset();
    }
    
    @Override
    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
}
