/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.text.UnicodeSet;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class UBiDiProps {
    private int[] indexes;
    private int[] mirrors;
    private byte[] jgArray;
    private Trie2_16 trie;
    private static final String DATA_NAME = "ubidi";
    private static final String DATA_TYPE = "icu";
    private static final String DATA_FILE_NAME = "ubidi.icu";
    private static final byte[] FMT = new byte[]{66, 105, 68, 105};
    private static final int IX_TRIE_SIZE = 2;
    private static final int IX_MIRROR_LENGTH = 3;
    private static final int IX_JG_START = 4;
    private static final int IX_JG_LIMIT = 5;
    private static final int IX_MAX_VALUES = 15;
    private static final int IX_TOP = 16;
    private static final int JT_SHIFT = 5;
    private static final int JOIN_CONTROL_SHIFT = 10;
    private static final int BIDI_CONTROL_SHIFT = 11;
    private static final int IS_MIRRORED_SHIFT = 12;
    private static final int MIRROR_DELTA_SHIFT = 13;
    private static final int MAX_JG_SHIFT = 16;
    private static final int CLASS_MASK = 31;
    private static final int JT_MASK = 224;
    private static final int MAX_JG_MASK = 0xFF0000;
    private static final int ESC_MIRROR_DELTA = -4;
    private static final int MIRROR_INDEX_SHIFT = 21;
    public static final UBiDiProps INSTANCE;

    private UBiDiProps() throws IOException {
        InputStream is2 = ICUData.getStream("data/icudt51b/ubidi.icu");
        BufferedInputStream b2 = new BufferedInputStream(is2, 4096);
        this.readData(b2);
        b2.close();
        is2.close();
    }

    private void readData(InputStream is2) throws IOException {
        int i2;
        DataInputStream inputStream = new DataInputStream(is2);
        ICUBinary.readHeader(inputStream, FMT, new IsAcceptable());
        int count = inputStream.readInt();
        if (count < 16) {
            throw new IOException("indexes[0] too small in ubidi.icu");
        }
        this.indexes = new int[count];
        this.indexes[0] = count;
        for (i2 = 1; i2 < count; ++i2) {
            this.indexes[i2] = inputStream.readInt();
        }
        this.trie = Trie2_16.createFromSerialized(inputStream);
        int expectedTrieLength = this.indexes[2];
        int trieLength = this.trie.getSerializedLength();
        if (trieLength > expectedTrieLength) {
            throw new IOException("ubidi.icu: not enough bytes for the trie");
        }
        inputStream.skipBytes(expectedTrieLength - trieLength);
        count = this.indexes[3];
        if (count > 0) {
            this.mirrors = new int[count];
            for (i2 = 0; i2 < count; ++i2) {
                this.mirrors[i2] = inputStream.readInt();
            }
        }
        count = this.indexes[5] - this.indexes[4];
        this.jgArray = new byte[count];
        for (i2 = 0; i2 < count; ++i2) {
            this.jgArray[i2] = inputStream.readByte();
        }
    }

    public final void addPropertyStarts(UnicodeSet set) {
        int i2;
        for (Trie2.Range range : this.trie) {
            if (range.leadSurrogate) break;
            set.add(range.startCodePoint);
        }
        int length = this.indexes[3];
        for (i2 = 0; i2 < length; ++i2) {
            int c2 = UBiDiProps.getMirrorCodePoint(this.mirrors[i2]);
            set.add(c2, c2 + 1);
        }
        int start = this.indexes[4];
        int limit = this.indexes[5];
        length = limit - start;
        byte prev = 0;
        for (i2 = 0; i2 < length; ++i2) {
            byte jg2 = this.jgArray[i2];
            if (jg2 != prev) {
                set.add(start);
                prev = jg2;
            }
            ++start;
        }
        if (prev != 0) {
            set.add(limit);
        }
    }

    public final int getMaxValue(int which) {
        int max = this.indexes[15];
        switch (which) {
            case 4096: {
                return max & 0x1F;
            }
            case 4102: {
                return (max & 0xFF0000) >> 16;
            }
            case 4103: {
                return (max & 0xE0) >> 5;
            }
        }
        return -1;
    }

    public final int getClass(int c2) {
        return UBiDiProps.getClassFromProps(this.trie.get(c2));
    }

    public final boolean isMirrored(int c2) {
        return UBiDiProps.getFlagFromProps(this.trie.get(c2), 12);
    }

    public final int getMirror(int c2) {
        int props = this.trie.get(c2);
        int delta = (short)props >> 13;
        if (delta != -4) {
            return c2 + delta;
        }
        int length = this.indexes[3];
        for (int i2 = 0; i2 < length; ++i2) {
            int m2 = this.mirrors[i2];
            int c22 = UBiDiProps.getMirrorCodePoint(m2);
            if (c2 == c22) {
                return UBiDiProps.getMirrorCodePoint(this.mirrors[UBiDiProps.getMirrorIndex(m2)]);
            }
            if (c2 < c22) break;
        }
        return c2;
    }

    public final boolean isBidiControl(int c2) {
        return UBiDiProps.getFlagFromProps(this.trie.get(c2), 11);
    }

    public final boolean isJoinControl(int c2) {
        return UBiDiProps.getFlagFromProps(this.trie.get(c2), 10);
    }

    public final int getJoiningType(int c2) {
        return (this.trie.get(c2) & 0xE0) >> 5;
    }

    public final int getJoiningGroup(int c2) {
        int start = this.indexes[4];
        int limit = this.indexes[5];
        if (start <= c2 && c2 < limit) {
            return this.jgArray[c2 - start] & 0xFF;
        }
        return 0;
    }

    private static final int getClassFromProps(int props) {
        return props & 0x1F;
    }

    private static final boolean getFlagFromProps(int props, int shift) {
        return (props >> shift & 1) != 0;
    }

    private static final int getMirrorCodePoint(int m2) {
        return m2 & 0x1FFFFF;
    }

    private static final int getMirrorIndex(int m2) {
        return m2 >>> 21;
    }

    static {
        try {
            INSTANCE = new UBiDiProps();
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    private static final class IsAcceptable
    implements ICUBinary.Authenticate {
        private IsAcceptable() {
        }

        public boolean isDataVersionAcceptable(byte[] version) {
            return version[0] == 2;
        }
    }
}

