package org.apache.commons.compress.compressors.deflate;

import org.apache.commons.compress.compressors.*;
import java.util.zip.*;
import java.io.*;
import org.apache.commons.compress.utils.*;

public class DeflateCompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private static final int MAGIC_1 = 120;
    private static final int MAGIC_2a = 1;
    private static final int MAGIC_2b = 94;
    private static final int MAGIC_2c = 156;
    private static final int MAGIC_2d = 218;
    private final CountingInputStream countingStream;
    private final InputStream in;
    private final Inflater inflater;
    
    public DeflateCompressorInputStream(final InputStream inputStream) {
        this(inputStream, new DeflateParameters());
    }
    
    public DeflateCompressorInputStream(final InputStream inputStream, final DeflateParameters parameters) {
        this.inflater = new Inflater(!parameters.withZlibHeader());
        this.in = new InflaterInputStream(this.countingStream = new CountingInputStream(inputStream), this.inflater);
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
        try {
            this.in.close();
        }
        finally {
            this.inflater.end();
        }
    }
    
    @Override
    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length > 3 && signature[0] == 120 && (signature[1] == 1 || signature[1] == 94 || signature[1] == -100 || signature[1] == -38);
    }
}
