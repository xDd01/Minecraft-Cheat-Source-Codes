/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2Writable;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.impl.Trie2_32;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.VersionInfo;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public final class Normalizer2Impl {
    private static final IsAcceptable IS_ACCEPTABLE = new IsAcceptable();
    private static final byte[] DATA_FORMAT = new byte[]{78, 114, 109, 50};
    private static final Trie2.ValueMapper segmentStarterMapper = new Trie2.ValueMapper(){

        public int map(int in2) {
            return in2 & Integer.MIN_VALUE;
        }
    };
    public static final int MIN_CCC_LCCC_CP = 768;
    public static final int MIN_YES_YES_WITH_CC = 65281;
    public static final int JAMO_VT = 65280;
    public static final int MIN_NORMAL_MAYBE_YES = 65024;
    public static final int JAMO_L = 1;
    public static final int MAX_DELTA = 64;
    public static final int IX_NORM_TRIE_OFFSET = 0;
    public static final int IX_EXTRA_DATA_OFFSET = 1;
    public static final int IX_SMALL_FCD_OFFSET = 2;
    public static final int IX_RESERVED3_OFFSET = 3;
    public static final int IX_TOTAL_SIZE = 7;
    public static final int IX_MIN_DECOMP_NO_CP = 8;
    public static final int IX_MIN_COMP_NO_MAYBE_CP = 9;
    public static final int IX_MIN_YES_NO = 10;
    public static final int IX_MIN_NO_NO = 11;
    public static final int IX_LIMIT_NO_NO = 12;
    public static final int IX_MIN_MAYBE_YES = 13;
    public static final int IX_MIN_YES_NO_MAPPINGS_ONLY = 14;
    public static final int IX_COUNT = 16;
    public static final int MAPPING_HAS_CCC_LCCC_WORD = 128;
    public static final int MAPPING_HAS_RAW_MAPPING = 64;
    public static final int MAPPING_NO_COMP_BOUNDARY_AFTER = 32;
    public static final int MAPPING_LENGTH_MASK = 31;
    public static final int COMP_1_LAST_TUPLE = 32768;
    public static final int COMP_1_TRIPLE = 1;
    public static final int COMP_1_TRAIL_LIMIT = 13312;
    public static final int COMP_1_TRAIL_MASK = 32766;
    public static final int COMP_1_TRAIL_SHIFT = 9;
    public static final int COMP_2_TRAIL_SHIFT = 6;
    public static final int COMP_2_TRAIL_MASK = 65472;
    private VersionInfo dataVersion;
    private int minDecompNoCP;
    private int minCompNoMaybeCP;
    private int minYesNo;
    private int minYesNoMappingsOnly;
    private int minNoNo;
    private int limitNoNo;
    private int minMaybeYes;
    private Trie2_16 normTrie;
    private String maybeYesCompositions;
    private String extraData;
    private byte[] smallFCD;
    private int[] tccc180;
    private Trie2_32 canonIterData;
    private ArrayList<UnicodeSet> canonStartSets;
    private static final int CANON_NOT_SEGMENT_STARTER = Integer.MIN_VALUE;
    private static final int CANON_HAS_COMPOSITIONS = 0x40000000;
    private static final int CANON_HAS_SET = 0x200000;
    private static final int CANON_VALUE_MASK = 0x1FFFFF;

    public Normalizer2Impl load(InputStream data) {
        try {
            int i2;
            BufferedInputStream bis2 = new BufferedInputStream(data);
            this.dataVersion = ICUBinary.readHeaderAndDataVersion(bis2, DATA_FORMAT, IS_ACCEPTABLE);
            DataInputStream ds2 = new DataInputStream(bis2);
            int indexesLength = ds2.readInt() / 4;
            if (indexesLength <= 13) {
                throw new IOException("Normalizer2 data: not enough indexes");
            }
            int[] inIndexes = new int[indexesLength];
            inIndexes[0] = indexesLength * 4;
            for (int i3 = 1; i3 < indexesLength; ++i3) {
                inIndexes[i3] = ds2.readInt();
            }
            this.minDecompNoCP = inIndexes[8];
            this.minCompNoMaybeCP = inIndexes[9];
            this.minYesNo = inIndexes[10];
            this.minYesNoMappingsOnly = inIndexes[14];
            this.minNoNo = inIndexes[11];
            this.limitNoNo = inIndexes[12];
            this.minMaybeYes = inIndexes[13];
            int offset = inIndexes[0];
            int nextOffset = inIndexes[1];
            this.normTrie = Trie2_16.createFromSerialized(ds2);
            int trieLength = this.normTrie.getSerializedLength();
            if (trieLength > nextOffset - offset) {
                throw new IOException("Normalizer2 data: not enough bytes for normTrie");
            }
            ds2.skipBytes(nextOffset - offset - trieLength);
            offset = nextOffset;
            nextOffset = inIndexes[2];
            int numChars = (nextOffset - offset) / 2;
            if (numChars != 0) {
                char[] chars = new char[numChars];
                for (i2 = 0; i2 < numChars; ++i2) {
                    chars[i2] = ds2.readChar();
                }
                this.maybeYesCompositions = new String(chars);
                this.extraData = this.maybeYesCompositions.substring(65024 - this.minMaybeYes);
            }
            offset = nextOffset;
            this.smallFCD = new byte[256];
            for (i2 = 0; i2 < 256; ++i2) {
                this.smallFCD[i2] = ds2.readByte();
            }
            this.tccc180 = new int[384];
            int bits = 0;
            int c2 = 0;
            while (c2 < 384) {
                if ((c2 & 0xFF) == 0) {
                    bits = this.smallFCD[c2 >> 8];
                }
                if (bits & true) {
                    int i4 = 0;
                    while (i4 < 32) {
                        this.tccc180[c2] = this.getFCD16FromNormData(c2) & 0xFF;
                        ++i4;
                        ++c2;
                    }
                } else {
                    c2 += 32;
                }
                bits >>= 1;
            }
            data.close();
            return this;
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    public Normalizer2Impl load(String name) {
        return this.load(ICUData.getRequiredStream(name));
    }

    public void addPropertyStarts(UnicodeSet set) {
        for (Trie2.Range range : this.normTrie) {
            if (range.leadSurrogate) break;
            set.add(range.startCodePoint);
        }
        for (int c2 = 44032; c2 < 55204; c2 += 28) {
            set.add(c2);
            set.add(c2 + 1);
        }
        set.add(55204);
    }

    public void addCanonIterPropertyStarts(UnicodeSet set) {
        this.ensureCanonIterData();
        Iterator<Trie2.Range> trieIterator = this.canonIterData.iterator(segmentStarterMapper);
        while (trieIterator.hasNext()) {
            Trie2.Range range = trieIterator.next();
            if (range.leadSurrogate) break;
            set.add(range.startCodePoint);
        }
    }

    public Trie2_16 getNormTrie() {
        return this.normTrie;
    }

    public synchronized Normalizer2Impl ensureCanonIterData() {
        if (this.canonIterData == null) {
            Trie2Writable newData = new Trie2Writable(0, 0);
            this.canonStartSets = new ArrayList();
            for (Trie2.Range range : this.normTrie) {
                if (range.leadSurrogate) break;
                int norm16 = range.value;
                if (norm16 == 0 || this.minYesNo <= norm16 && norm16 < this.minNoNo) continue;
                for (int c2 = range.startCodePoint; c2 <= range.endCodePoint; ++c2) {
                    int oldValue;
                    int newValue = oldValue = newData.get(c2);
                    if (norm16 >= this.minMaybeYes) {
                        newValue |= Integer.MIN_VALUE;
                        if (norm16 < 65024) {
                            newValue |= 0x40000000;
                        }
                    } else if (norm16 < this.minYesNo) {
                        newValue |= 0x40000000;
                    } else {
                        int c22 = c2;
                        int norm16_2 = norm16;
                        while (this.limitNoNo <= norm16_2 && norm16_2 < this.minMaybeYes) {
                            c22 = this.mapAlgorithmic(c22, norm16_2);
                            norm16_2 = this.getNorm16(c22);
                        }
                        if (this.minYesNo <= norm16_2 && norm16_2 < this.limitNoNo) {
                            char firstUnit = this.extraData.charAt(norm16_2);
                            int length = firstUnit & 0x1F;
                            if ((firstUnit & 0x80) != 0 && c2 == c22 && (this.extraData.charAt(norm16_2 - 1) & 0xFF) != 0) {
                                newValue |= Integer.MIN_VALUE;
                            }
                            if (length != 0) {
                                int limit = ++norm16_2 + length;
                                c22 = this.extraData.codePointAt(norm16_2);
                                this.addToStartSet(newData, c2, c22);
                                if (norm16_2 >= this.minNoNo) {
                                    while ((norm16_2 += Character.charCount(c22)) < limit) {
                                        c22 = this.extraData.codePointAt(norm16_2);
                                        int c2Value = newData.get(c22);
                                        if ((c2Value & Integer.MIN_VALUE) != 0) continue;
                                        newData.set(c22, c2Value | Integer.MIN_VALUE);
                                    }
                                }
                            }
                        } else {
                            this.addToStartSet(newData, c2, c22);
                        }
                    }
                    if (newValue == oldValue) continue;
                    newData.set(c2, newValue);
                }
            }
            this.canonIterData = newData.toTrie2_32();
        }
        return this;
    }

    public int getNorm16(int c2) {
        return this.normTrie.get(c2);
    }

    public int getCompQuickCheck(int norm16) {
        if (norm16 < this.minNoNo || 65281 <= norm16) {
            return 1;
        }
        if (this.minMaybeYes <= norm16) {
            return 2;
        }
        return 0;
    }

    public boolean isCompNo(int norm16) {
        return this.minNoNo <= norm16 && norm16 < this.minMaybeYes;
    }

    public boolean isDecompYes(int norm16) {
        return norm16 < this.minYesNo || this.minMaybeYes <= norm16;
    }

    public int getCC(int norm16) {
        if (norm16 >= 65024) {
            return norm16 & 0xFF;
        }
        if (norm16 < this.minNoNo || this.limitNoNo <= norm16) {
            return 0;
        }
        return this.getCCFromNoNo(norm16);
    }

    public static int getCCFromYesOrMaybe(int norm16) {
        return norm16 >= 65024 ? norm16 & 0xFF : 0;
    }

    public int getFCD16(int c2) {
        if (c2 < 0) {
            return 0;
        }
        if (c2 < 384) {
            return this.tccc180[c2];
        }
        if (c2 <= 65535 && !this.singleLeadMightHaveNonZeroFCD16(c2)) {
            return 0;
        }
        return this.getFCD16FromNormData(c2);
    }

    public int getFCD16FromBelow180(int c2) {
        return this.tccc180[c2];
    }

    public boolean singleLeadMightHaveNonZeroFCD16(int lead) {
        byte bits = this.smallFCD[lead >> 8];
        if (bits == 0) {
            return false;
        }
        return (bits >> (lead >> 5 & 7) & 1) != 0;
    }

    public int getFCD16FromNormData(int c2) {
        int norm16;
        while (true) {
            if ((norm16 = this.getNorm16(c2)) <= this.minYesNo) {
                return 0;
            }
            if (norm16 >= 65024) {
                return (norm16 &= 0xFF) | norm16 << 8;
            }
            if (norm16 >= this.minMaybeYes) {
                return 0;
            }
            if (!this.isDecompNoAlgorithmic(norm16)) break;
            c2 = this.mapAlgorithmic(c2, norm16);
        }
        char firstUnit = this.extraData.charAt(norm16);
        if ((firstUnit & 0x1F) == 0) {
            return 511;
        }
        int fcd16 = firstUnit >> 8;
        if ((firstUnit & 0x80) != 0) {
            fcd16 |= this.extraData.charAt(norm16 - 1) & 0xFF00;
        }
        return fcd16;
    }

    public String getDecomposition(int c2) {
        int norm16;
        int decomp = -1;
        while (c2 >= this.minDecompNoCP && !this.isDecompYes(norm16 = this.getNorm16(c2))) {
            if (this.isHangul(norm16)) {
                StringBuilder buffer = new StringBuilder();
                Hangul.decompose(c2, buffer);
                return buffer.toString();
            }
            if (this.isDecompNoAlgorithmic(norm16)) {
                decomp = c2 = this.mapAlgorithmic(c2, norm16);
                continue;
            }
            int length = this.extraData.charAt(norm16++) & 0x1F;
            return this.extraData.substring(norm16, norm16 + length);
        }
        if (decomp < 0) {
            return null;
        }
        return UTF16.valueOf(decomp);
    }

    public String getRawDecomposition(int c2) {
        int norm16;
        if (c2 < this.minDecompNoCP || this.isDecompYes(norm16 = this.getNorm16(c2))) {
            return null;
        }
        if (this.isHangul(norm16)) {
            StringBuilder buffer = new StringBuilder();
            Hangul.getRawDecomposition(c2, buffer);
            return buffer.toString();
        }
        if (this.isDecompNoAlgorithmic(norm16)) {
            return UTF16.valueOf(this.mapAlgorithmic(c2, norm16));
        }
        char firstUnit = this.extraData.charAt(norm16);
        int mLength = firstUnit & 0x1F;
        if ((firstUnit & 0x40) != 0) {
            int rawMapping = norm16 - (firstUnit >> 7 & 1) - 1;
            char rm0 = this.extraData.charAt(rawMapping);
            if (rm0 <= '\u001f') {
                return this.extraData.substring(rawMapping - rm0, rawMapping);
            }
            StringBuilder buffer = new StringBuilder(mLength - 1).append(rm0);
            return buffer.append(this.extraData, norm16 += 3, norm16 + mLength - 2).toString();
        }
        return this.extraData.substring(++norm16, norm16 + mLength);
    }

    public boolean isCanonSegmentStarter(int c2) {
        return this.canonIterData.get(c2) >= 0;
    }

    public boolean getCanonStartSet(int c2, UnicodeSet set) {
        int canonValue = this.canonIterData.get(c2) & Integer.MAX_VALUE;
        if (canonValue == 0) {
            return false;
        }
        set.clear();
        int value = canonValue & 0x1FFFFF;
        if ((canonValue & 0x200000) != 0) {
            set.addAll(this.canonStartSets.get(value));
        } else if (value != 0) {
            set.add(value);
        }
        if ((canonValue & 0x40000000) != 0) {
            int norm16 = this.getNorm16(c2);
            if (norm16 == 1) {
                int syllable = 44032 + (c2 - 4352) * 588;
                set.add(syllable, syllable + 588 - 1);
            } else {
                this.addComposites(this.getCompositionsList(norm16), set);
            }
        }
        return true;
    }

    public int decompose(CharSequence s2, int src, int limit, ReorderingBuffer buffer) {
        block11: {
            int minNoCP = this.minDecompNoCP;
            int c2 = 0;
            int norm16 = 0;
            int prevBoundary = src;
            int prevCC = 0;
            while (true) {
                int cc2;
                int prevSrc = src;
                while (src != limit) {
                    char c22;
                    c2 = s2.charAt(src);
                    if (c2 < minNoCP || this.isMostDecompYesAndZeroCC(norm16 = this.normTrie.getFromU16SingleLead((char)c2))) {
                        ++src;
                        continue;
                    }
                    if (!UTF16.isSurrogate((char)c2)) break;
                    if (UTF16Plus.isSurrogateLead(c2)) {
                        if (src + 1 != limit && Character.isLowSurrogate(c22 = s2.charAt(src + 1))) {
                            c2 = Character.toCodePoint((char)c2, c22);
                        }
                    } else if (prevSrc < src && Character.isHighSurrogate(c22 = s2.charAt(src - 1))) {
                        --src;
                        c2 = Character.toCodePoint(c22, (char)c2);
                    }
                    if (!this.isMostDecompYesAndZeroCC(norm16 = this.getNorm16(c2))) break;
                    src += Character.charCount(c2);
                }
                if (src != prevSrc) {
                    if (buffer != null) {
                        buffer.flushAndAppendZeroCC(s2, prevSrc, src);
                    } else {
                        prevCC = 0;
                        prevBoundary = src;
                    }
                }
                if (src == limit) break block11;
                src += Character.charCount(c2);
                if (buffer != null) {
                    this.decompose(c2, norm16, buffer);
                    continue;
                }
                if (!this.isDecompYes(norm16) || prevCC > (cc2 = Normalizer2Impl.getCCFromYesOrMaybe(norm16)) && cc2 != 0) break;
                prevCC = cc2;
                if (cc2 > 1) continue;
                prevBoundary = src;
            }
            return prevBoundary;
        }
        return src;
    }

    public void decomposeAndAppend(CharSequence s2, boolean doDecompose, ReorderingBuffer buffer) {
        int cc2;
        int limit = s2.length();
        if (limit == 0) {
            return;
        }
        if (doDecompose) {
            this.decompose(s2, 0, limit, buffer);
            return;
        }
        int c2 = Character.codePointAt(s2, 0);
        int src = 0;
        int prevCC = cc2 = this.getCC(this.getNorm16(c2));
        int firstCC = cc2;
        while (cc2 != 0) {
            prevCC = cc2;
            if ((src += Character.charCount(c2)) >= limit) break;
            c2 = Character.codePointAt(s2, src);
            cc2 = this.getCC(this.getNorm16(c2));
        }
        buffer.append(s2, 0, src, firstCC, prevCC);
        buffer.append(s2, src, limit);
    }

    /*
     * Enabled aggressive block sorting
     */
    public boolean compose(CharSequence s2, int src, int limit, boolean onlyContiguous, boolean doCompose, ReorderingBuffer buffer) {
        int minNoMaybeCP = this.minCompNoMaybeCP;
        int prevBoundary = src;
        int c2 = 0;
        int norm16 = 0;
        int prevCC = 0;
        while (true) {
            int prevSrc;
            block35: {
                prevSrc = src;
                while (src != limit) {
                    char c22;
                    c2 = s2.charAt(src);
                    if (c2 < minNoMaybeCP || this.isCompYesAndZeroCC(norm16 = this.normTrie.getFromU16SingleLead((char)c2))) {
                        ++src;
                        continue;
                    }
                    if (!UTF16.isSurrogate((char)c2)) break;
                    if (UTF16Plus.isSurrogateLead(c2)) {
                        if (src + 1 != limit && Character.isLowSurrogate(c22 = s2.charAt(src + 1))) {
                            c2 = Character.toCodePoint((char)c2, c22);
                        }
                    } else if (prevSrc < src && Character.isHighSurrogate(c22 = s2.charAt(src - 1))) {
                        --src;
                        c2 = Character.toCodePoint(c22, (char)c2);
                    }
                    if (!this.isCompYesAndZeroCC(norm16 = this.getNorm16(c2))) break;
                    src += Character.charCount(c2);
                }
                if (src != prevSrc) {
                    if (src == limit) {
                        if (!doCompose) return true;
                        buffer.flushAndAppendZeroCC(s2, prevSrc, src);
                        return true;
                    }
                    prevBoundary = src - 1;
                    if (Character.isLowSurrogate(s2.charAt(prevBoundary)) && prevSrc < prevBoundary && Character.isHighSurrogate(s2.charAt(prevBoundary - 1))) {
                        --prevBoundary;
                    }
                    if (doCompose) {
                        buffer.flushAndAppendZeroCC(s2, prevSrc, prevBoundary);
                        buffer.append(s2, prevBoundary, src);
                    } else {
                        prevCC = 0;
                    }
                    prevSrc = src;
                } else if (src == limit) {
                    return true;
                }
                src += Character.charCount(c2);
                if (Normalizer2Impl.isJamoVT(norm16) && prevBoundary != prevSrc) {
                    char prev = s2.charAt(prevSrc - 1);
                    boolean needToDecompose = false;
                    if (c2 < 4519) {
                        if ((prev = (char)(prev - 4352)) < '\u0013') {
                            char t2;
                            if (!doCompose) {
                                return false;
                            }
                            char syllable = (char)(44032 + (prev * 21 + (c2 - 4449)) * 28);
                            if (src != limit && (t2 = (char)(s2.charAt(src) - 4519)) < '\u001c') {
                                syllable = (char)(syllable + t2);
                                prevBoundary = ++src;
                                buffer.setLastChar(syllable);
                                continue;
                            }
                            needToDecompose = true;
                        }
                    } else if (Hangul.isHangulWithoutJamoT(prev)) {
                        if (!doCompose) {
                            return false;
                        }
                        buffer.setLastChar((char)(prev + c2 - 4519));
                        prevBoundary = src;
                        continue;
                    }
                    if (!needToDecompose) {
                        if (doCompose) {
                            buffer.append((char)c2);
                            continue;
                        }
                        prevCC = 0;
                        continue;
                    }
                }
                if (norm16 >= 65281) {
                    int cc2 = norm16 & 0xFF;
                    if (onlyContiguous && (doCompose ? buffer.getLastCC() : prevCC) == 0 && prevBoundary < prevSrc && this.getTrailCCFromCompYesAndZeroCC(s2, prevBoundary, prevSrc) > cc2) {
                        if (!doCompose) {
                            return false;
                        }
                        break block35;
                    } else {
                        if (doCompose) {
                            buffer.append(c2, cc2);
                            continue;
                        }
                        if (prevCC > cc2) return false;
                        prevCC = cc2;
                        continue;
                    }
                }
                if (!doCompose && !this.isMaybeOrNonZeroCC(norm16)) {
                    return false;
                }
            }
            if (this.hasCompBoundaryBefore(c2, norm16)) {
                prevBoundary = prevSrc;
            } else if (doCompose) {
                buffer.removeSuffix(prevSrc - prevBoundary);
            }
            src = this.findNextCompBoundary(s2, src, limit);
            int recomposeStartIndex = buffer.length();
            this.decomposeShort(s2, prevBoundary, src, buffer);
            this.recompose(buffer, recomposeStartIndex, onlyContiguous);
            if (!doCompose) {
                if (!buffer.equals(s2, prevBoundary, src)) {
                    return false;
                }
                buffer.remove();
                prevCC = 0;
            }
            prevBoundary = src;
        }
    }

    public int composeQuickCheck(CharSequence s2, int src, int limit, boolean onlyContiguous, boolean doSpan) {
        int prevBoundary;
        block10: {
            int qcResult = 0;
            int minNoMaybeCP = this.minCompNoMaybeCP;
            prevBoundary = src;
            int c2 = 0;
            int norm16 = 0;
            int prevCC = 0;
            while (true) {
                int prevSrc = src;
                while (true) {
                    char c22;
                    if (src == limit) {
                        return src << 1 | qcResult;
                    }
                    c2 = s2.charAt(src);
                    if (c2 < minNoMaybeCP || this.isCompYesAndZeroCC(norm16 = this.normTrie.getFromU16SingleLead((char)c2))) {
                        ++src;
                        continue;
                    }
                    if (!UTF16.isSurrogate((char)c2)) break;
                    if (UTF16Plus.isSurrogateLead(c2)) {
                        if (src + 1 != limit && Character.isLowSurrogate(c22 = s2.charAt(src + 1))) {
                            c2 = Character.toCodePoint((char)c2, c22);
                        }
                    } else if (prevSrc < src && Character.isHighSurrogate(c22 = s2.charAt(src - 1))) {
                        --src;
                        c2 = Character.toCodePoint(c22, (char)c2);
                    }
                    if (!this.isCompYesAndZeroCC(norm16 = this.getNorm16(c2))) break;
                    src += Character.charCount(c2);
                }
                if (src != prevSrc) {
                    prevBoundary = src - 1;
                    if (Character.isLowSurrogate(s2.charAt(prevBoundary)) && prevSrc < prevBoundary && Character.isHighSurrogate(s2.charAt(prevBoundary - 1))) {
                        --prevBoundary;
                    }
                    prevCC = 0;
                    prevSrc = src;
                }
                src += Character.charCount(c2);
                if (!this.isMaybeOrNonZeroCC(norm16)) break block10;
                int cc2 = Normalizer2Impl.getCCFromYesOrMaybe(norm16);
                if (onlyContiguous && cc2 != 0 && prevCC == 0 && prevBoundary < prevSrc && this.getTrailCCFromCompYesAndZeroCC(s2, prevBoundary, prevSrc) > cc2 || prevCC > cc2 && cc2 != 0) break block10;
                prevCC = cc2;
                if (norm16 >= 65281) continue;
                if (doSpan) break;
                qcResult = 1;
            }
            return prevBoundary << 1;
        }
        return prevBoundary << 1;
    }

    public void composeAndAppend(CharSequence s2, boolean doCompose, boolean onlyContiguous, ReorderingBuffer buffer) {
        int firstStarterInSrc;
        int src = 0;
        int limit = s2.length();
        if (!buffer.isEmpty() && 0 != (firstStarterInSrc = this.findNextCompBoundary(s2, 0, limit))) {
            int lastStarterInDest = this.findPreviousCompBoundary(buffer.getStringBuilder(), buffer.length());
            StringBuilder middle = new StringBuilder(buffer.length() - lastStarterInDest + firstStarterInSrc + 16);
            middle.append(buffer.getStringBuilder(), lastStarterInDest, buffer.length());
            buffer.removeSuffix(buffer.length() - lastStarterInDest);
            middle.append(s2, 0, firstStarterInSrc);
            this.compose(middle, 0, middle.length(), onlyContiguous, true, buffer);
            src = firstStarterInSrc;
        }
        if (doCompose) {
            this.compose(s2, src, limit, onlyContiguous, true, buffer);
        } else {
            buffer.append(s2, src, limit);
        }
    }

    public int makeFCD(CharSequence s2, int src, int limit, ReorderingBuffer buffer) {
        int prevBoundary = src;
        int c2 = 0;
        int prevFCD16 = 0;
        int fcd16 = 0;
        while (true) {
            int prevSrc = src;
            while (src != limit) {
                c2 = s2.charAt(src);
                if (c2 < 768) {
                    prevFCD16 = ~c2;
                    ++src;
                    continue;
                }
                if (!this.singleLeadMightHaveNonZeroFCD16(c2)) {
                    prevFCD16 = 0;
                    ++src;
                    continue;
                }
                if (UTF16.isSurrogate((char)c2)) {
                    char c22;
                    if (UTF16Plus.isSurrogateLead(c2)) {
                        if (src + 1 != limit && Character.isLowSurrogate(c22 = s2.charAt(src + 1))) {
                            c2 = Character.toCodePoint((char)c2, c22);
                        }
                    } else if (prevSrc < src && Character.isHighSurrogate(c22 = s2.charAt(src - 1))) {
                        --src;
                        c2 = Character.toCodePoint(c22, (char)c2);
                    }
                }
                if ((fcd16 = this.getFCD16FromNormData(c2)) > 255) break;
                prevFCD16 = fcd16;
                src += Character.charCount(c2);
            }
            if (src != prevSrc) {
                if (src == limit) {
                    if (buffer == null) break;
                    buffer.flushAndAppendZeroCC(s2, prevSrc, src);
                    break;
                }
                prevBoundary = src;
                if (prevFCD16 < 0) {
                    int prev = ~prevFCD16;
                    int n2 = prevFCD16 = prev < 384 ? this.tccc180[prev] : this.getFCD16FromNormData(prev);
                    if (prevFCD16 > 1) {
                        --prevBoundary;
                    }
                } else {
                    int p2 = src - 1;
                    if (Character.isLowSurrogate(s2.charAt(p2)) && prevSrc < p2 && Character.isHighSurrogate(s2.charAt(p2 - 1))) {
                        prevFCD16 = this.getFCD16FromNormData(Character.toCodePoint(s2.charAt(--p2), s2.charAt(p2 + 1)));
                    }
                    if (prevFCD16 > 1) {
                        prevBoundary = p2;
                    }
                }
                if (buffer != null) {
                    buffer.flushAndAppendZeroCC(s2, prevSrc, prevBoundary);
                    buffer.append(s2, prevBoundary, src);
                }
                prevSrc = src;
            } else if (src == limit) break;
            src += Character.charCount(c2);
            if ((prevFCD16 & 0xFF) <= fcd16 >> 8) {
                if ((fcd16 & 0xFF) <= 1) {
                    prevBoundary = src;
                }
                if (buffer != null) {
                    buffer.appendZeroCC(c2);
                }
                prevFCD16 = fcd16;
                continue;
            }
            if (buffer == null) {
                return prevBoundary;
            }
            buffer.removeSuffix(prevSrc - prevBoundary);
            src = this.findNextFCDBoundary(s2, src, limit);
            this.decomposeShort(s2, prevBoundary, src, buffer);
            prevBoundary = src;
            prevFCD16 = 0;
        }
        return src;
    }

    public void makeFCDAndAppend(CharSequence s2, boolean doMakeFCD, ReorderingBuffer buffer) {
        int firstBoundaryInSrc;
        int src = 0;
        int limit = s2.length();
        if (!buffer.isEmpty() && 0 != (firstBoundaryInSrc = this.findNextFCDBoundary(s2, 0, limit))) {
            int lastBoundaryInDest = this.findPreviousFCDBoundary(buffer.getStringBuilder(), buffer.length());
            StringBuilder middle = new StringBuilder(buffer.length() - lastBoundaryInDest + firstBoundaryInSrc + 16);
            middle.append(buffer.getStringBuilder(), lastBoundaryInDest, buffer.length());
            buffer.removeSuffix(buffer.length() - lastBoundaryInDest);
            middle.append(s2, 0, firstBoundaryInSrc);
            this.makeFCD(middle, 0, middle.length(), buffer);
            src = firstBoundaryInSrc;
        }
        if (doMakeFCD) {
            this.makeFCD(s2, src, limit, buffer);
        } else {
            buffer.append(s2, src, limit);
        }
    }

    public boolean hasDecompBoundary(int c2, boolean before) {
        int norm16;
        while (true) {
            if (c2 < this.minDecompNoCP) {
                return true;
            }
            norm16 = this.getNorm16(c2);
            if (this.isHangul(norm16) || this.isDecompYesAndZeroCC(norm16)) {
                return true;
            }
            if (norm16 > 65024) {
                return false;
            }
            if (!this.isDecompNoAlgorithmic(norm16)) break;
            c2 = this.mapAlgorithmic(c2, norm16);
        }
        char firstUnit = this.extraData.charAt(norm16);
        if ((firstUnit & 0x1F) == 0) {
            return false;
        }
        if (!before) {
            if (firstUnit > '\u01ff') {
                return false;
            }
            if (firstUnit <= '\u00ff') {
                return true;
            }
        }
        return (firstUnit & 0x80) == 0 || (this.extraData.charAt(norm16 - 1) & 0xFF00) == 0;
    }

    public boolean isDecompInert(int c2) {
        return this.isDecompYesAndZeroCC(this.getNorm16(c2));
    }

    public boolean hasCompBoundaryBefore(int c2) {
        return c2 < this.minCompNoMaybeCP || this.hasCompBoundaryBefore(c2, this.getNorm16(c2));
    }

    public boolean hasCompBoundaryAfter(int c2, boolean onlyContiguous, boolean testInert) {
        int norm16;
        while (true) {
            if (Normalizer2Impl.isInert(norm16 = this.getNorm16(c2))) {
                return true;
            }
            if (norm16 <= this.minYesNo) {
                return this.isHangul(norm16) && !Hangul.isHangulWithoutJamoT((char)c2);
            }
            if (norm16 >= (testInert ? this.minNoNo : this.minMaybeYes)) {
                return false;
            }
            if (!this.isDecompNoAlgorithmic(norm16)) break;
            c2 = this.mapAlgorithmic(c2, norm16);
        }
        char firstUnit = this.extraData.charAt(norm16);
        return (firstUnit & 0x20) == 0 && (!onlyContiguous || firstUnit <= '\u01ff');
    }

    public boolean hasFCDBoundaryBefore(int c2) {
        return c2 < 768 || this.getFCD16(c2) <= 255;
    }

    public boolean hasFCDBoundaryAfter(int c2) {
        int fcd16 = this.getFCD16(c2);
        return fcd16 <= 1 || (fcd16 & 0xFF) == 0;
    }

    public boolean isFCDInert(int c2) {
        return this.getFCD16(c2) <= 1;
    }

    private boolean isMaybe(int norm16) {
        return this.minMaybeYes <= norm16 && norm16 <= 65280;
    }

    private boolean isMaybeOrNonZeroCC(int norm16) {
        return norm16 >= this.minMaybeYes;
    }

    private static boolean isInert(int norm16) {
        return norm16 == 0;
    }

    private static boolean isJamoL(int norm16) {
        return norm16 == 1;
    }

    private static boolean isJamoVT(int norm16) {
        return norm16 == 65280;
    }

    private boolean isHangul(int norm16) {
        return norm16 == this.minYesNo;
    }

    private boolean isCompYesAndZeroCC(int norm16) {
        return norm16 < this.minNoNo;
    }

    private boolean isDecompYesAndZeroCC(int norm16) {
        return norm16 < this.minYesNo || norm16 == 65280 || this.minMaybeYes <= norm16 && norm16 <= 65024;
    }

    private boolean isMostDecompYesAndZeroCC(int norm16) {
        return norm16 < this.minYesNo || norm16 == 65024 || norm16 == 65280;
    }

    private boolean isDecompNoAlgorithmic(int norm16) {
        return norm16 >= this.limitNoNo;
    }

    private int getCCFromNoNo(int norm16) {
        if ((this.extraData.charAt(norm16) & 0x80) != 0) {
            return this.extraData.charAt(norm16 - 1) & 0xFF;
        }
        return 0;
    }

    int getTrailCCFromCompYesAndZeroCC(CharSequence s2, int cpStart, int cpLimit) {
        int c2 = cpStart == cpLimit - 1 ? s2.charAt(cpStart) : Character.codePointAt(s2, cpStart);
        int prevNorm16 = this.getNorm16(c2);
        if (prevNorm16 <= this.minYesNo) {
            return 0;
        }
        return this.extraData.charAt(prevNorm16) >> 8;
    }

    private int mapAlgorithmic(int c2, int norm16) {
        return c2 + norm16 - (this.minMaybeYes - 64 - 1);
    }

    private int getCompositionsListForDecompYes(int norm16) {
        if (norm16 == 0 || 65024 <= norm16) {
            return -1;
        }
        if ((norm16 -= this.minMaybeYes) < 0) {
            norm16 += 65024;
        }
        return norm16;
    }

    private int getCompositionsListForComposite(int norm16) {
        char firstUnit = this.extraData.charAt(norm16);
        return 65024 - this.minMaybeYes + norm16 + 1 + (firstUnit & 0x1F);
    }

    private int getCompositionsList(int norm16) {
        return this.isDecompYes(norm16) ? this.getCompositionsListForDecompYes(norm16) : this.getCompositionsListForComposite(norm16);
    }

    public void decomposeShort(CharSequence s2, int src, int limit, ReorderingBuffer buffer) {
        while (src < limit) {
            int c2 = Character.codePointAt(s2, src);
            src += Character.charCount(c2);
            this.decompose(c2, this.getNorm16(c2), buffer);
        }
    }

    private void decompose(int c2, int norm16, ReorderingBuffer buffer) {
        block3: {
            while (true) {
                if (this.isDecompYes(norm16)) {
                    buffer.append(c2, Normalizer2Impl.getCCFromYesOrMaybe(norm16));
                    break block3;
                }
                if (this.isHangul(norm16)) {
                    Hangul.decompose(c2, buffer);
                    break block3;
                }
                if (!this.isDecompNoAlgorithmic(norm16)) break;
                c2 = this.mapAlgorithmic(c2, norm16);
                norm16 = this.getNorm16(c2);
            }
            char firstUnit = this.extraData.charAt(norm16);
            int length = firstUnit & 0x1F;
            int trailCC = firstUnit >> 8;
            int leadCC = (firstUnit & 0x80) != 0 ? this.extraData.charAt(norm16 - 1) >> 8 : 0;
            buffer.append(this.extraData, ++norm16, norm16 + length, leadCC, trailCC);
        }
    }

    private static int combine(String compositions, int list, int trail) {
        block9: {
            if (trail < 13312) {
                char firstUnit;
                int key1 = trail << 1;
                while (key1 > (firstUnit = compositions.charAt(list))) {
                    list += 2 + (firstUnit & '\u0001');
                }
                if (key1 == (firstUnit & 0x7FFE)) {
                    if ((firstUnit & '\u0001') != 0) {
                        return compositions.charAt(list + 1) << 16 | compositions.charAt(list + 2);
                    }
                    return compositions.charAt(list + 1);
                }
            } else {
                char secondUnit;
                int key1 = 13312 + (trail >> 9 & 0xFFFFFFFE);
                int key2 = trail << 6 & 0xFFFF;
                while (true) {
                    char firstUnit;
                    if (key1 > (firstUnit = compositions.charAt(list))) {
                        list += 2 + (firstUnit & '\u0001');
                        continue;
                    }
                    if (key1 != (firstUnit & 0x7FFE)) break block9;
                    secondUnit = compositions.charAt(list + 1);
                    if (key2 <= secondUnit) break;
                    if ((firstUnit & 0x8000) == 0) {
                        list += 3;
                        continue;
                    }
                    break block9;
                    break;
                }
                if (key2 == (secondUnit & 0xFFC0)) {
                    return (secondUnit & 0xFFFF003F) << 16 | compositions.charAt(list + 2);
                }
            }
        }
        return -1;
    }

    private void addComposites(int list, UnicodeSet set) {
        char firstUnit;
        do {
            int compositeAndFwd;
            if (((firstUnit = this.maybeYesCompositions.charAt(list)) & '\u0001') == 0) {
                compositeAndFwd = this.maybeYesCompositions.charAt(list + 1);
                list += 2;
            } else {
                compositeAndFwd = (this.maybeYesCompositions.charAt(list + 1) & 0xFFFF003F) << 16 | this.maybeYesCompositions.charAt(list + 2);
                list += 3;
            }
            int composite = compositeAndFwd >> 1;
            if ((compositeAndFwd & '\u0001') != 0) {
                this.addComposites(this.getCompositionsListForComposite(this.getNorm16(composite)), set);
            }
            set.add(composite);
        } while ((firstUnit & 0x8000) == 0);
    }

    private void recompose(ReorderingBuffer buffer, int recomposeStartIndex, boolean onlyContiguous) {
        int p2 = recomposeStartIndex;
        StringBuilder sb2 = buffer.getStringBuilder();
        if (p2 == sb2.length()) {
            return;
        }
        int compositionsList = -1;
        int starter = -1;
        boolean starterIsSupplementary = false;
        int prevCC = 0;
        while (true) {
            int c2 = sb2.codePointAt(p2);
            p2 += Character.charCount(c2);
            int norm16 = this.getNorm16(c2);
            int cc2 = Normalizer2Impl.getCCFromYesOrMaybe(norm16);
            if (this.isMaybe(norm16) && compositionsList >= 0 && (prevCC < cc2 || prevCC == 0)) {
                int pRemove;
                if (Normalizer2Impl.isJamoVT(norm16)) {
                    char prev;
                    if (c2 < 4519 && (prev = (char)(sb2.charAt(starter) - 4352)) < '\u0013') {
                        char t2;
                        pRemove = p2 - 1;
                        char syllable = (char)(44032 + (prev * 21 + (c2 - 4449)) * 28);
                        if (p2 != sb2.length() && (t2 = (char)(sb2.charAt(p2) - 4519)) < '\u001c') {
                            ++p2;
                            syllable = (char)(syllable + t2);
                        }
                        sb2.setCharAt(starter, syllable);
                        sb2.delete(pRemove, p2);
                        p2 = pRemove;
                    }
                    if (p2 == sb2.length()) break;
                    compositionsList = -1;
                    continue;
                }
                int compositeAndFwd = Normalizer2Impl.combine(this.maybeYesCompositions, compositionsList, c2);
                if (compositeAndFwd >= 0) {
                    int composite = compositeAndFwd >> 1;
                    pRemove = p2 - Character.charCount(c2);
                    sb2.delete(pRemove, p2);
                    p2 = pRemove;
                    if (starterIsSupplementary) {
                        if (composite > 65535) {
                            sb2.setCharAt(starter, UTF16.getLeadSurrogate(composite));
                            sb2.setCharAt(starter + 1, UTF16.getTrailSurrogate(composite));
                        } else {
                            sb2.setCharAt(starter, (char)c2);
                            sb2.deleteCharAt(starter + 1);
                            starterIsSupplementary = false;
                            --p2;
                        }
                    } else if (composite > 65535) {
                        starterIsSupplementary = true;
                        sb2.setCharAt(starter, UTF16.getLeadSurrogate(composite));
                        sb2.insert(starter + 1, UTF16.getTrailSurrogate(composite));
                        ++p2;
                    } else {
                        sb2.setCharAt(starter, (char)composite);
                    }
                    if (p2 == sb2.length()) break;
                    if ((compositeAndFwd & 1) != 0) {
                        compositionsList = this.getCompositionsListForComposite(this.getNorm16(composite));
                        continue;
                    }
                    compositionsList = -1;
                    continue;
                }
            }
            prevCC = cc2;
            if (p2 == sb2.length()) break;
            if (cc2 == 0) {
                compositionsList = this.getCompositionsListForDecompYes(norm16);
                if (compositionsList < 0) continue;
                if (c2 <= 65535) {
                    starterIsSupplementary = false;
                    starter = p2 - 1;
                    continue;
                }
                starterIsSupplementary = true;
                starter = p2 - 2;
                continue;
            }
            if (!onlyContiguous) continue;
            compositionsList = -1;
        }
        buffer.flush();
    }

    public int composePair(int a2, int b2) {
        int list;
        int norm16 = this.getNorm16(a2);
        if (Normalizer2Impl.isInert(norm16)) {
            return -1;
        }
        if (norm16 < this.minYesNoMappingsOnly) {
            if (Normalizer2Impl.isJamoL(norm16)) {
                if (0 <= (b2 -= 4449) && b2 < 21) {
                    return 44032 + ((a2 - 4352) * 21 + b2) * 28;
                }
                return -1;
            }
            if (this.isHangul(norm16)) {
                if (Hangul.isHangulWithoutJamoT((char)a2) && 0 < (b2 -= 4519) && b2 < 28) {
                    return a2 + b2;
                }
                return -1;
            }
            list = norm16;
            if (norm16 > this.minYesNo) {
                list += 1 + (this.extraData.charAt(list) & 0x1F);
            }
            list += 65024 - this.minMaybeYes;
        } else {
            if (norm16 < this.minMaybeYes || 65024 <= norm16) {
                return -1;
            }
            list = norm16 - this.minMaybeYes;
        }
        if (b2 < 0 || 0x10FFFF < b2) {
            return -1;
        }
        return Normalizer2Impl.combine(this.maybeYesCompositions, list, b2) >> 1;
    }

    private boolean hasCompBoundaryBefore(int c2, int norm16) {
        while (true) {
            if (this.isCompYesAndZeroCC(norm16)) {
                return true;
            }
            if (this.isMaybeOrNonZeroCC(norm16)) {
                return false;
            }
            if (!this.isDecompNoAlgorithmic(norm16)) break;
            c2 = this.mapAlgorithmic(c2, norm16);
            norm16 = this.getNorm16(c2);
        }
        char firstUnit = this.extraData.charAt(norm16);
        if ((firstUnit & 0x1F) == 0) {
            return false;
        }
        if ((firstUnit & 0x80) != 0 && (this.extraData.charAt(norm16 - 1) & 0xFF00) != 0) {
            return false;
        }
        return this.isCompYesAndZeroCC(this.getNorm16(Character.codePointAt(this.extraData, norm16 + 1)));
    }

    private int findPreviousCompBoundary(CharSequence s2, int p2) {
        while (p2 > 0) {
            int c2 = Character.codePointBefore(s2, p2);
            p2 -= Character.charCount(c2);
            if (!this.hasCompBoundaryBefore(c2)) continue;
            break;
        }
        return p2;
    }

    private int findNextCompBoundary(CharSequence s2, int p2, int limit) {
        int norm16;
        int c2;
        while (p2 < limit && !this.hasCompBoundaryBefore(c2 = Character.codePointAt(s2, p2), norm16 = this.normTrie.get(c2))) {
            p2 += Character.charCount(c2);
        }
        return p2;
    }

    private int findPreviousFCDBoundary(CharSequence s2, int p2) {
        while (p2 > 0) {
            int c2 = Character.codePointBefore(s2, p2);
            p2 -= Character.charCount(c2);
            if (c2 >= 768 && this.getFCD16(c2) > 255) continue;
            break;
        }
        return p2;
    }

    private int findNextFCDBoundary(CharSequence s2, int p2, int limit) {
        int c2;
        while (p2 < limit && (c2 = Character.codePointAt(s2, p2)) >= 768 && this.getFCD16(c2) > 255) {
            p2 += Character.charCount(c2);
        }
        return p2;
    }

    private void addToStartSet(Trie2Writable newData, int origin, int decompLead) {
        int canonValue = newData.get(decompLead);
        if ((canonValue & 0x3FFFFF) == 0 && origin != 0) {
            newData.set(decompLead, canonValue | origin);
        } else {
            UnicodeSet set;
            if ((canonValue & 0x200000) == 0) {
                int firstOrigin = canonValue & 0x1FFFFF;
                canonValue = canonValue & 0xFFE00000 | 0x200000 | this.canonStartSets.size();
                newData.set(decompLead, canonValue);
                set = new UnicodeSet();
                this.canonStartSets.add(set);
                if (firstOrigin != 0) {
                    set.add(firstOrigin);
                }
            } else {
                set = this.canonStartSets.get(canonValue & 0x1FFFFF);
            }
            set.add(origin);
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

    public static final class UTF16Plus {
        public static boolean isSurrogateLead(int c2) {
            return (c2 & 0x400) == 0;
        }

        public static boolean equal(CharSequence s1, CharSequence s2) {
            if (s1 == s2) {
                return true;
            }
            int length = s1.length();
            if (length != s2.length()) {
                return false;
            }
            for (int i2 = 0; i2 < length; ++i2) {
                if (s1.charAt(i2) == s2.charAt(i2)) continue;
                return false;
            }
            return true;
        }

        public static boolean equal(CharSequence s1, int start1, int limit1, CharSequence s2, int start2, int limit2) {
            if (limit1 - start1 != limit2 - start2) {
                return false;
            }
            if (s1 == s2 && start1 == start2) {
                return true;
            }
            while (start1 < limit1) {
                if (s1.charAt(start1++) == s2.charAt(start2++)) continue;
                return false;
            }
            return true;
        }
    }

    public static final class ReorderingBuffer
    implements Appendable {
        private final Normalizer2Impl impl;
        private final Appendable app;
        private final StringBuilder str;
        private final boolean appIsStringBuilder;
        private int reorderStart;
        private int lastCC;
        private int codePointStart;
        private int codePointLimit;

        public ReorderingBuffer(Normalizer2Impl ni2, Appendable dest, int destCapacity) {
            this.impl = ni2;
            this.app = dest;
            if (this.app instanceof StringBuilder) {
                this.appIsStringBuilder = true;
                this.str = (StringBuilder)dest;
                this.str.ensureCapacity(destCapacity);
                this.reorderStart = 0;
                if (this.str.length() == 0) {
                    this.lastCC = 0;
                } else {
                    this.setIterator();
                    this.lastCC = this.previousCC();
                    if (this.lastCC > 1) {
                        while (this.previousCC() > 1) {
                        }
                    }
                    this.reorderStart = this.codePointLimit;
                }
            } else {
                this.appIsStringBuilder = false;
                this.str = new StringBuilder();
                this.reorderStart = 0;
                this.lastCC = 0;
            }
        }

        public boolean isEmpty() {
            return this.str.length() == 0;
        }

        public int length() {
            return this.str.length();
        }

        public int getLastCC() {
            return this.lastCC;
        }

        public StringBuilder getStringBuilder() {
            return this.str;
        }

        public boolean equals(CharSequence s2, int start, int limit) {
            return UTF16Plus.equal(this.str, 0, this.str.length(), s2, start, limit);
        }

        public void setLastChar(char c2) {
            this.str.setCharAt(this.str.length() - 1, c2);
        }

        public void append(int c2, int cc2) {
            if (this.lastCC <= cc2 || cc2 == 0) {
                this.str.appendCodePoint(c2);
                this.lastCC = cc2;
                if (cc2 <= 1) {
                    this.reorderStart = this.str.length();
                }
            } else {
                this.insert(c2, cc2);
            }
        }

        public void append(CharSequence s2, int start, int limit, int leadCC, int trailCC) {
            if (start == limit) {
                return;
            }
            if (this.lastCC <= leadCC || leadCC == 0) {
                if (trailCC <= 1) {
                    this.reorderStart = this.str.length() + (limit - start);
                } else if (leadCC <= 1) {
                    this.reorderStart = this.str.length() + 1;
                }
                this.str.append(s2, start, limit);
                this.lastCC = trailCC;
            } else {
                int c2 = Character.codePointAt(s2, start);
                start += Character.charCount(c2);
                this.insert(c2, leadCC);
                while (start < limit) {
                    leadCC = (start += Character.charCount(c2 = Character.codePointAt(s2, start))) < limit ? Normalizer2Impl.getCCFromYesOrMaybe(this.impl.getNorm16(c2)) : trailCC;
                    this.append(c2, leadCC);
                }
            }
        }

        public ReorderingBuffer append(char c2) {
            this.str.append(c2);
            this.lastCC = 0;
            this.reorderStart = this.str.length();
            return this;
        }

        public void appendZeroCC(int c2) {
            this.str.appendCodePoint(c2);
            this.lastCC = 0;
            this.reorderStart = this.str.length();
        }

        public ReorderingBuffer append(CharSequence s2) {
            if (s2.length() != 0) {
                this.str.append(s2);
                this.lastCC = 0;
                this.reorderStart = this.str.length();
            }
            return this;
        }

        public ReorderingBuffer append(CharSequence s2, int start, int limit) {
            if (start != limit) {
                this.str.append(s2, start, limit);
                this.lastCC = 0;
                this.reorderStart = this.str.length();
            }
            return this;
        }

        public void flush() {
            if (this.appIsStringBuilder) {
                this.reorderStart = this.str.length();
            } else {
                try {
                    this.app.append(this.str);
                    this.str.setLength(0);
                    this.reorderStart = 0;
                }
                catch (IOException e2) {
                    throw new RuntimeException(e2);
                }
            }
            this.lastCC = 0;
        }

        public ReorderingBuffer flushAndAppendZeroCC(CharSequence s2, int start, int limit) {
            if (this.appIsStringBuilder) {
                this.str.append(s2, start, limit);
                this.reorderStart = this.str.length();
            } else {
                try {
                    this.app.append(this.str).append(s2, start, limit);
                    this.str.setLength(0);
                    this.reorderStart = 0;
                }
                catch (IOException e2) {
                    throw new RuntimeException(e2);
                }
            }
            this.lastCC = 0;
            return this;
        }

        public void remove() {
            this.str.setLength(0);
            this.lastCC = 0;
            this.reorderStart = 0;
        }

        public void removeSuffix(int suffixLength) {
            int oldLength = this.str.length();
            this.str.delete(oldLength - suffixLength, oldLength);
            this.lastCC = 0;
            this.reorderStart = this.str.length();
        }

        private void insert(int c2, int cc2) {
            this.setIterator();
            this.skipPrevious();
            while (this.previousCC() > cc2) {
            }
            if (c2 <= 65535) {
                this.str.insert(this.codePointLimit, (char)c2);
                if (cc2 <= 1) {
                    this.reorderStart = this.codePointLimit + 1;
                }
            } else {
                this.str.insert(this.codePointLimit, Character.toChars(c2));
                if (cc2 <= 1) {
                    this.reorderStart = this.codePointLimit + 2;
                }
            }
        }

        private void setIterator() {
            this.codePointStart = this.str.length();
        }

        private void skipPrevious() {
            this.codePointLimit = this.codePointStart;
            this.codePointStart = this.str.offsetByCodePoints(this.codePointStart, -1);
        }

        private int previousCC() {
            this.codePointLimit = this.codePointStart;
            if (this.reorderStart >= this.codePointStart) {
                return 0;
            }
            int c2 = this.str.codePointBefore(this.codePointStart);
            this.codePointStart -= Character.charCount(c2);
            if (c2 < 768) {
                return 0;
            }
            return Normalizer2Impl.getCCFromYesOrMaybe(this.impl.getNorm16(c2));
        }
    }

    public static final class Hangul {
        public static final int JAMO_L_BASE = 4352;
        public static final int JAMO_V_BASE = 4449;
        public static final int JAMO_T_BASE = 4519;
        public static final int HANGUL_BASE = 44032;
        public static final int JAMO_L_COUNT = 19;
        public static final int JAMO_V_COUNT = 21;
        public static final int JAMO_T_COUNT = 28;
        public static final int JAMO_L_LIMIT = 4371;
        public static final int JAMO_V_LIMIT = 4470;
        public static final int JAMO_VT_COUNT = 588;
        public static final int HANGUL_COUNT = 11172;
        public static final int HANGUL_LIMIT = 55204;

        public static boolean isHangul(int c2) {
            return 44032 <= c2 && c2 < 55204;
        }

        public static boolean isHangulWithoutJamoT(char c2) {
            return (c2 = (char)(c2 - 44032)) < '\u2ba4' && c2 % 28 == 0;
        }

        public static boolean isJamoL(int c2) {
            return 4352 <= c2 && c2 < 4371;
        }

        public static boolean isJamoV(int c2) {
            return 4449 <= c2 && c2 < 4470;
        }

        public static int decompose(int c2, Appendable buffer) {
            try {
                int c22 = (c2 -= 44032) % 28;
                buffer.append((char)(4352 + (c2 /= 28) / 21));
                buffer.append((char)(4449 + c2 % 21));
                if (c22 == 0) {
                    return 2;
                }
                buffer.append((char)(4519 + c22));
                return 3;
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }

        public static void getRawDecomposition(int c2, Appendable buffer) {
            try {
                int orig = c2;
                int c22 = (c2 -= 44032) % 28;
                if (c22 == 0) {
                    buffer.append((char)(4352 + (c2 /= 28) / 21));
                    buffer.append((char)(4449 + c2 % 21));
                } else {
                    buffer.append((char)(orig - c22));
                    buffer.append((char)(4519 + c22));
                }
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
    }
}

