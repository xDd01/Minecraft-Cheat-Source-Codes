package org.apache.commons.compress.utils;

import java.util.zip.*;
import java.io.*;

public class ChecksumCalculatingInputStream extends InputStream
{
    private final InputStream in;
    private final Checksum checksum;
    
    public ChecksumCalculatingInputStream(final Checksum checksum, final InputStream in) {
        if (checksum == null) {
            throw new NullPointerException("Parameter checksum must not be null");
        }
        if (in == null) {
            throw new NullPointerException("Parameter in must not be null");
        }
        this.checksum = checksum;
        this.in = in;
    }
    
    @Override
    public int read() throws IOException {
        final int ret = this.in.read();
        if (ret >= 0) {
            this.checksum.update(ret);
        }
        return ret;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int ret = this.in.read(b, off, len);
        if (ret >= 0) {
            this.checksum.update(b, off, ret);
        }
        return ret;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (this.read() >= 0) {
            return 1L;
        }
        return 0L;
    }
    
    public long getValue() {
        return this.checksum.getValue();
    }
}
