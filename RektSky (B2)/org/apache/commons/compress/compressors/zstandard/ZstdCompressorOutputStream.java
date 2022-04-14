package org.apache.commons.compress.compressors.zstandard;

import org.apache.commons.compress.compressors.*;
import com.github.luben.zstd.*;
import java.io.*;

public class ZstdCompressorOutputStream extends CompressorOutputStream
{
    private final ZstdOutputStream encOS;
    
    public ZstdCompressorOutputStream(final OutputStream outStream, final int level, final boolean closeFrameOnFlush, final boolean useChecksum) throws IOException {
        this.encOS = new ZstdOutputStream(outStream, level, closeFrameOnFlush, useChecksum);
    }
    
    public ZstdCompressorOutputStream(final OutputStream outStream, final int level, final boolean closeFrameOnFlush) throws IOException {
        this.encOS = new ZstdOutputStream(outStream, level, closeFrameOnFlush);
    }
    
    public ZstdCompressorOutputStream(final OutputStream outStream, final int level) throws IOException {
        this.encOS = new ZstdOutputStream(outStream, level);
    }
    
    public ZstdCompressorOutputStream(final OutputStream outStream) throws IOException {
        this.encOS = new ZstdOutputStream(outStream);
    }
    
    @Override
    public void close() throws IOException {
        this.encOS.close();
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.encOS.write(b);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws IOException {
        this.encOS.write(buf, off, len);
    }
    
    @Override
    public String toString() {
        return this.encOS.toString();
    }
    
    @Override
    public void flush() throws IOException {
        this.encOS.flush();
    }
}
