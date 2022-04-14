/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.BytesDictionaryMatcher;
import com.ibm.icu.text.CharsDictionaryMatcher;
import com.ibm.icu.text.DictionaryMatcher;
import com.ibm.icu.util.UResourceBundle;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class DictionaryData {
    public static final int TRIE_TYPE_BYTES = 0;
    public static final int TRIE_TYPE_UCHARS = 1;
    public static final int TRIE_TYPE_MASK = 7;
    public static final int TRIE_HAS_VALUES = 8;
    public static final int TRANSFORM_NONE = 0;
    public static final int TRANSFORM_TYPE_OFFSET = 0x1000000;
    public static final int TRANSFORM_TYPE_MASK = 0x7F000000;
    public static final int TRANSFORM_OFFSET_MASK = 0x1FFFFF;
    public static final int IX_STRING_TRIE_OFFSET = 0;
    public static final int IX_RESERVED1_OFFSET = 1;
    public static final int IX_RESERVED2_OFFSET = 2;
    public static final int IX_TOTAL_SIZE = 3;
    public static final int IX_TRIE_TYPE = 4;
    public static final int IX_TRANSFORM = 5;
    public static final int IX_RESERVED6 = 6;
    public static final int IX_RESERVED7 = 7;
    public static final int IX_COUNT = 8;
    private static final byte[] DATA_FORMAT_ID = new byte[]{68, 105, 99, 116};

    private DictionaryData() {
    }

    public static DictionaryMatcher loadDictionaryFor(String dictType) throws IOException {
        ICUResourceBundle rb2 = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/brkitr");
        String dictFileName = rb2.getStringWithFallback("dictionaries/" + dictType);
        dictFileName = "data/icudt51b/brkitr/" + dictFileName;
        InputStream is2 = ICUData.getStream(dictFileName);
        ICUBinary.readHeader(is2, DATA_FORMAT_ID, null);
        DataInputStream s2 = new DataInputStream(is2);
        int[] indexes = new int[8];
        for (int i2 = 0; i2 < 8; ++i2) {
            indexes[i2] = s2.readInt();
        }
        int offset = indexes[0];
        Assert.assrt(offset >= 32);
        if (offset > 32) {
            int diff = offset - 32;
            s2.skipBytes(diff);
        }
        int trieType = indexes[4] & 7;
        int totalSize = indexes[3] - offset;
        DictionaryMatcher m2 = null;
        if (trieType == 0) {
            int i3;
            int transform = indexes[5];
            byte[] data = new byte[totalSize];
            for (i3 = 0; i3 < data.length; ++i3) {
                data[i3] = s2.readByte();
            }
            Assert.assrt(i3 == totalSize);
            m2 = new BytesDictionaryMatcher(data, transform);
        } else if (trieType == 1) {
            Assert.assrt(totalSize % 2 == 0);
            int num = totalSize / 2;
            char[] data = new char[totalSize / 2];
            for (int i4 = 0; i4 < num; ++i4) {
                data[i4] = s2.readChar();
            }
            m2 = new CharsDictionaryMatcher(new String(data));
        } else {
            m2 = null;
        }
        s2.close();
        is2.close();
        return m2;
    }
}

