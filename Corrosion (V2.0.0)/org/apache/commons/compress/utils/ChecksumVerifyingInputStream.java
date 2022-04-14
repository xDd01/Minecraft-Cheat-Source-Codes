/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Checksum;

public class ChecksumVerifyingInputStream
extends InputStream {
    private final InputStream in;
    private long bytesRemaining;
    private final long expectedChecksum;
    private final Checksum checksum;

    public ChecksumVerifyingInputStream(Checksum checksum, InputStream in2, long size, long expectedChecksum) {
        this.checksum = checksum;
        this.in = in2;
        this.expectedChecksum = expectedChecksum;
        this.bytesRemaining = size;
    }

    public int read() throws IOException {
        if (this.bytesRemaining <= 0L) {
            return -1;
        }
        int ret = this.in.read();
        if (ret >= 0) {
            this.checksum.update(ret);
            --this.bytesRemaining;
        }
        if (this.bytesRemaining == 0L && this.expectedChecksum != this.checksum.getValue()) {
            throw new IOException("Checksum verification failed");
        }
        return ret;
    }

    public int read(byte[] b2) throws IOException {
        return this.read(b2, 0, b2.length);
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        int ret = this.in.read(b2, off, len);
        if (ret >= 0) {
            this.checksum.update(b2, off, ret);
            this.bytesRemaining -= (long)ret;
        }
        if (this.bytesRemaining <= 0L && this.expectedChecksum != this.checksum.getValue()) {
            throw new IOException("Checksum verification failed");
        }
        return ret;
    }

    public long skip(long n2) throws IOException {
        if (this.read() >= 0) {
            return 1L;
        }
        return 0L;
    }

    public void close() throws IOException {
        this.in.close();
    }
}

