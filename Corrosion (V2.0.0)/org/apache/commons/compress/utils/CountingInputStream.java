/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream
extends FilterInputStream {
    private long bytesRead;

    public CountingInputStream(InputStream in2) {
        super(in2);
    }

    public int read() throws IOException {
        int r2 = this.in.read();
        if (r2 >= 0) {
            this.count(1L);
        }
        return r2;
    }

    public int read(byte[] b2) throws IOException {
        return this.read(b2, 0, b2.length);
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        int r2 = this.in.read(b2, off, len);
        if (r2 >= 0) {
            this.count(r2);
        }
        return r2;
    }

    protected final void count(long read) {
        if (read != -1L) {
            this.bytesRead += read;
        }
    }

    public long getBytesRead() {
        return this.bytesRead;
    }
}

