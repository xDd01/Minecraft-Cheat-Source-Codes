/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;

class BitStream {
    private final InputStream in;
    private long bitCache;
    private int bitCacheSize;
    private static final int[] MASKS = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255};

    BitStream(InputStream in2) {
        this.in = in2;
    }

    private boolean fillCache() throws IOException {
        long nextByte;
        boolean filled = false;
        while (this.bitCacheSize <= 56 && (nextByte = (long)this.in.read()) != -1L) {
            filled = true;
            this.bitCache |= nextByte << this.bitCacheSize;
            this.bitCacheSize += 8;
        }
        return filled;
    }

    int nextBit() throws IOException {
        if (this.bitCacheSize == 0 && !this.fillCache()) {
            return -1;
        }
        int bit2 = (int)(this.bitCache & 1L);
        this.bitCache >>>= 1;
        --this.bitCacheSize;
        return bit2;
    }

    int nextBits(int n2) throws IOException {
        if (this.bitCacheSize < n2 && !this.fillCache()) {
            return -1;
        }
        int bits = (int)(this.bitCache & (long)MASKS[n2]);
        this.bitCache >>>= n2;
        this.bitCacheSize -= n2;
        return bits;
    }

    int nextByte() throws IOException {
        return this.nextBits(8);
    }
}

