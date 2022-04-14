/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.sevenz;

import java.util.LinkedList;
import org.apache.commons.compress.archivers.sevenz.BindPair;
import org.apache.commons.compress.archivers.sevenz.Coder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class Folder {
    Coder[] coders;
    long totalInputStreams;
    long totalOutputStreams;
    BindPair[] bindPairs;
    long[] packedStreams;
    long[] unpackSizes;
    boolean hasCrc;
    long crc;
    int numUnpackSubStreams;

    Folder() {
    }

    Iterable<Coder> getOrderedCoders() {
        LinkedList<Coder> l2 = new LinkedList<Coder>();
        int current = (int)this.packedStreams[0];
        while (current != -1) {
            l2.addLast(this.coders[current]);
            int pair = this.findBindPairForOutStream(current);
            current = pair != -1 ? (int)this.bindPairs[pair].inIndex : -1;
        }
        return l2;
    }

    int findBindPairForInStream(int index) {
        for (int i2 = 0; i2 < this.bindPairs.length; ++i2) {
            if (this.bindPairs[i2].inIndex != (long)index) continue;
            return i2;
        }
        return -1;
    }

    int findBindPairForOutStream(int index) {
        for (int i2 = 0; i2 < this.bindPairs.length; ++i2) {
            if (this.bindPairs[i2].outIndex != (long)index) continue;
            return i2;
        }
        return -1;
    }

    long getUnpackSize() {
        if (this.totalOutputStreams == 0L) {
            return 0L;
        }
        for (int i2 = (int)this.totalOutputStreams - 1; i2 >= 0; --i2) {
            if (this.findBindPairForOutStream(i2) >= 0) continue;
            return this.unpackSizes[i2];
        }
        return 0L;
    }
}

