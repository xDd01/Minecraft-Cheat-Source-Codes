package org.apache.commons.compress.compressors.xz;

import org.apache.commons.compress.compressors.*;
import org.tukaani.xz.*;
import java.io.*;

public class XZCompressorOutputStream extends CompressorOutputStream
{
    private final XZOutputStream out;
    
    public XZCompressorOutputStream(final OutputStream outputStream) throws IOException {
        this.out = new XZOutputStream(outputStream, (FilterOptions)new LZMA2Options());
    }
    
    public XZCompressorOutputStream(final OutputStream outputStream, final int preset) throws IOException {
        this.out = new XZOutputStream(outputStream, (FilterOptions)new LZMA2Options(preset));
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws IOException {
        this.out.write(buf, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public void finish() throws IOException {
        this.out.finish();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
