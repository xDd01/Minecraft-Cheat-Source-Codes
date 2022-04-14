/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream
extends InputStream {
    private final InputStream in;
    private long bytesRemaining;

    public BoundedInputStream(InputStream in2, long size) {
        this.in = in2;
        this.bytesRemaining = size;
    }

    public int read() throws IOException {
        if (this.bytesRemaining > 0L) {
            --this.bytesRemaining;
            return this.in.read();
        }
        return -1;
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        int bytesRead;
        if (this.bytesRemaining == 0L) {
            return -1;
        }
        int bytesToRead = len;
        if ((long)bytesToRead > this.bytesRemaining) {
            bytesToRead = (int)this.bytesRemaining;
        }
        if ((bytesRead = this.in.read(b2, off, bytesToRead)) >= 0) {
            this.bytesRemaining -= (long)bytesRead;
        }
        return bytesRead;
    }

    public void close() {
    }
}

