/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream
extends FilterOutputStream {
    private long bytesWritten = 0L;

    public CountingOutputStream(OutputStream out) {
        super(out);
    }

    public void write(int b2) throws IOException {
        this.out.write(b2);
        this.count(1L);
    }

    public void write(byte[] b2) throws IOException {
        this.write(b2, 0, b2.length);
    }

    public void write(byte[] b2, int off, int len) throws IOException {
        this.out.write(b2, off, len);
        this.count(len);
    }

    protected void count(long written) {
        if (written != -1L) {
            this.bytesWritten += written;
        }
    }

    public long getBytesWritten() {
        return this.bytesWritten;
    }
}

