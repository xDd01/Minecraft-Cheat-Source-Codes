/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.Trie2;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Trie2_16
extends Trie2 {
    Trie2_16() {
    }

    public static Trie2_16 createFromSerialized(InputStream is2) throws IOException {
        return (Trie2_16)Trie2.createFromSerialized(is2);
    }

    public final int get(int codePoint) {
        if (codePoint >= 0) {
            if (codePoint < 55296 || codePoint > 56319 && codePoint <= 65535) {
                int ix2 = this.index[codePoint >> 5];
                ix2 = (ix2 << 2) + (codePoint & 0x1F);
                char value = this.index[ix2];
                return value;
            }
            if (codePoint <= 65535) {
                int ix3 = this.index[2048 + (codePoint - 55296 >> 5)];
                ix3 = (ix3 << 2) + (codePoint & 0x1F);
                char value = this.index[ix3];
                return value;
            }
            if (codePoint < this.highStart) {
                int ix4 = 2080 + (codePoint >> 11);
                ix4 = this.index[ix4];
                ix4 += codePoint >> 5 & 0x3F;
                ix4 = this.index[ix4];
                ix4 = (ix4 << 2) + (codePoint & 0x1F);
                char value = this.index[ix4];
                return value;
            }
            if (codePoint <= 0x10FFFF) {
                char value = this.index[this.highValueIndex];
                return value;
            }
        }
        return this.errorValue;
    }

    public int getFromU16SingleLead(char codeUnit) {
        int ix2 = this.index[codeUnit >> 5];
        ix2 = (ix2 << 2) + (codeUnit & 0x1F);
        char value = this.index[ix2];
        return value;
    }

    public int serialize(OutputStream os2) throws IOException {
        DataOutputStream dos = new DataOutputStream(os2);
        int bytesWritten = 0;
        bytesWritten += this.serializeHeader(dos);
        for (int i2 = 0; i2 < this.dataLength; ++i2) {
            dos.writeChar(this.index[this.data16 + i2]);
        }
        return bytesWritten += this.dataLength * 2;
    }

    public int getSerializedLength() {
        return 16 + (this.header.indexLength + this.dataLength) * 2;
    }

    int rangeEnd(int startingCP, int limit, int value) {
        int cp2 = startingCP;
        int block = 0;
        int index2Block = 0;
        block0: while (cp2 < limit) {
            if (cp2 < 55296 || cp2 > 56319 && cp2 <= 65535) {
                index2Block = 0;
                block = this.index[cp2 >> 5] << 2;
            } else if (cp2 < 65535) {
                index2Block = 2048;
                block = this.index[index2Block + (cp2 - 55296 >> 5)] << 2;
            } else if (cp2 < this.highStart) {
                int ix2 = 2080 + (cp2 >> 11);
                index2Block = this.index[ix2];
                block = this.index[index2Block + (cp2 >> 5 & 0x3F)] << 2;
            } else {
                if (value != this.index[this.highValueIndex]) break;
                cp2 = limit;
                break;
            }
            if (index2Block == this.index2NullOffset) {
                if (value != this.initialValue) break;
                cp2 += 2048;
                continue;
            }
            if (block == this.dataNullOffset) {
                if (value != this.initialValue) break;
                cp2 += 32;
                continue;
            }
            int startIx = block + (cp2 & 0x1F);
            int limitIx = block + 32;
            for (int ix3 = startIx; ix3 < limitIx; ++ix3) {
                if (this.index[ix3] == value) continue;
                cp2 += ix3 - startIx;
                break block0;
            }
            cp2 += limitIx - startIx;
        }
        if (cp2 > limit) {
            cp2 = limit;
        }
        return cp2 - 1;
    }
}

