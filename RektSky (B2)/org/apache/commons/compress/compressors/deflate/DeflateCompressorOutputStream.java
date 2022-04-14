package org.apache.commons.compress.compressors.deflate;

import org.apache.commons.compress.compressors.*;
import java.util.zip.*;
import java.io.*;

public class DeflateCompressorOutputStream extends CompressorOutputStream
{
    private final DeflaterOutputStream out;
    private final Deflater deflater;
    
    public DeflateCompressorOutputStream(final OutputStream outputStream) throws IOException {
        this(outputStream, new DeflateParameters());
    }
    
    public DeflateCompressorOutputStream(final OutputStream outputStream, final DeflateParameters parameters) throws IOException {
        this.deflater = new Deflater(parameters.getCompressionLevel(), !parameters.withZlibHeader());
        this.out = new DeflaterOutputStream(outputStream, this.deflater);
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
        try {
            this.out.close();
        }
        finally {
            this.deflater.end();
        }
    }
}
