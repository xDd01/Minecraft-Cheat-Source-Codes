/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnicodeSetStringSpan {
    public static final int FWD = 32;
    public static final int BACK = 16;
    public static final int UTF16 = 8;
    public static final int CONTAINED = 2;
    public static final int NOT_CONTAINED = 1;
    public static final int ALL = 63;
    public static final int FWD_UTF16_CONTAINED = 42;
    public static final int FWD_UTF16_NOT_CONTAINED = 41;
    public static final int BACK_UTF16_CONTAINED = 26;
    public static final int BACK_UTF16_NOT_CONTAINED = 25;
    static final short ALL_CP_CONTAINED = 255;
    static final short LONG_SPAN = 254;
    private UnicodeSet spanSet;
    private UnicodeSet spanNotSet;
    private ArrayList<String> strings;
    private short[] spanLengths;
    private int maxLength16;
    private boolean all;
    private OffsetList offsets;

    public UnicodeSetStringSpan(UnicodeSet set, ArrayList<String> setStrings, int which) {
        int spanLength;
        int i2;
        this.spanSet = new UnicodeSet(0, 0x10FFFF);
        this.strings = setStrings;
        this.all = which == 63;
        this.spanSet.retainAll(set);
        if (0 != (which & 1)) {
            this.spanNotSet = this.spanSet;
        }
        this.offsets = new OffsetList();
        int stringsLength = this.strings.size();
        boolean someRelevant = false;
        for (i2 = 0; i2 < stringsLength; ++i2) {
            String string = this.strings.get(i2);
            int length16 = string.length();
            spanLength = this.spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
            if (spanLength < length16) {
                someRelevant = true;
            }
            if (0 == (which & 8) || length16 <= this.maxLength16) continue;
            this.maxLength16 = length16;
        }
        if (!someRelevant) {
            this.maxLength16 = 0;
            return;
        }
        if (this.all) {
            this.spanSet.freeze();
        }
        int allocSize = this.all ? stringsLength * 2 : stringsLength;
        this.spanLengths = new short[allocSize];
        int spanBackLengthsOffset = this.all ? stringsLength : 0;
        for (i2 = 0; i2 < stringsLength; ++i2) {
            String string = this.strings.get(i2);
            int length16 = string.length();
            spanLength = this.spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
            if (spanLength < length16) {
                int c2;
                if (0 != (which & 8)) {
                    if (0 != (which & 2)) {
                        if (0 != (which & 0x20)) {
                            this.spanLengths[i2] = UnicodeSetStringSpan.makeSpanLengthByte(spanLength);
                        }
                        if (0 != (which & 0x10)) {
                            spanLength = length16 - this.spanSet.spanBack(string, length16, UnicodeSet.SpanCondition.CONTAINED);
                            this.spanLengths[spanBackLengthsOffset + i2] = UnicodeSetStringSpan.makeSpanLengthByte(spanLength);
                        }
                    } else {
                        this.spanLengths[spanBackLengthsOffset + i2] = 0;
                        this.spanLengths[i2] = 0;
                    }
                }
                if (0 == (which & 1)) continue;
                if (0 != (which & 0x20)) {
                    c2 = string.codePointAt(0);
                    this.addToSpanNotSet(c2);
                }
                if (0 == (which & 0x10)) continue;
                c2 = string.codePointBefore(length16);
                this.addToSpanNotSet(c2);
                continue;
            }
            if (this.all) {
                this.spanLengths[spanBackLengthsOffset + i2] = 255;
                this.spanLengths[i2] = 255;
                continue;
            }
            this.spanLengths[i2] = 255;
        }
        if (this.all) {
            this.spanNotSet.freeze();
        }
    }

    public UnicodeSetStringSpan(UnicodeSetStringSpan otherStringSpan, ArrayList<String> newParentSetStrings) {
        this.spanSet = otherStringSpan.spanSet;
        this.strings = newParentSetStrings;
        this.maxLength16 = otherStringSpan.maxLength16;
        this.all = true;
        this.spanNotSet = otherStringSpan.spanNotSet == otherStringSpan.spanSet ? this.spanSet : (UnicodeSet)otherStringSpan.spanNotSet.clone();
        this.offsets = new OffsetList();
        this.spanLengths = (short[])otherStringSpan.spanLengths.clone();
    }

    public boolean needsStringSpanUTF16() {
        return this.maxLength16 != 0;
    }

    public boolean contains(int c2) {
        return this.spanSet.contains(c2);
    }

    private void addToSpanNotSet(int c2) {
        if (this.spanNotSet == null || this.spanNotSet == this.spanSet) {
            if (this.spanSet.contains(c2)) {
                return;
            }
            this.spanNotSet = this.spanSet.cloneAsThawed();
        }
        this.spanNotSet.add(c2);
    }

    public synchronized int span(CharSequence s2, int start, int length, UnicodeSet.SpanCondition spanCondition) {
        if (spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            return this.spanNot(s2, start, length);
        }
        int spanLength = this.spanSet.span(s2.subSequence(start, start + length), UnicodeSet.SpanCondition.CONTAINED);
        if (spanLength == length) {
            return length;
        }
        int initSize = 0;
        if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
            initSize = this.maxLength16;
        }
        this.offsets.setMaxLength(initSize);
        int pos = start + spanLength;
        int rest = length - spanLength;
        int stringsLength = this.strings.size();
        while (true) {
            int i2;
            if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
                block1: for (i2 = 0; i2 < stringsLength; ++i2) {
                    int overlap = this.spanLengths[i2];
                    if (overlap == 255) continue;
                    String string = this.strings.get(i2);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                        overlap = string.offsetByCodePoints(overlap, -1);
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int inc = length16 - overlap; inc <= rest; ++inc) {
                        if (!this.offsets.containsOffset(inc) && UnicodeSetStringSpan.matches16CPB(s2, pos - overlap, length, string, length16)) {
                            if (inc == rest) {
                                return length;
                            }
                            this.offsets.addOffset(inc);
                        }
                        if (overlap == 0) continue block1;
                        --overlap;
                    }
                }
            } else {
                int maxInc = 0;
                int maxOverlap = 0;
                block3: for (i2 = 0; i2 < stringsLength; ++i2) {
                    int overlap = this.spanLengths[i2];
                    String string = this.strings.get(i2);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int inc = length16 - overlap; inc <= rest && overlap >= maxOverlap; --overlap, ++inc) {
                        if (overlap <= maxOverlap && inc <= maxInc || !UnicodeSetStringSpan.matches16CPB(s2, pos - overlap, length, string, length16)) continue;
                        maxInc = inc;
                        maxOverlap = overlap;
                        continue block3;
                    }
                }
                if (maxInc != 0 || maxOverlap != 0) {
                    pos += maxInc;
                    if ((rest -= maxInc) == 0) {
                        return length;
                    }
                    spanLength = 0;
                    continue;
                }
            }
            if (spanLength != 0 || pos == 0) {
                if (this.offsets.isEmpty()) {
                    return pos - start;
                }
            } else {
                if (this.offsets.isEmpty()) {
                    spanLength = this.spanSet.span(s2.subSequence(pos, pos + rest), UnicodeSet.SpanCondition.CONTAINED);
                    if (spanLength == rest || spanLength == 0) {
                        return pos + spanLength - start;
                    }
                    pos += spanLength;
                    rest -= spanLength;
                    continue;
                }
                spanLength = UnicodeSetStringSpan.spanOne(this.spanSet, s2, pos, rest);
                if (spanLength > 0) {
                    if (spanLength == rest) {
                        return length;
                    }
                    pos += spanLength;
                    rest -= spanLength;
                    this.offsets.shift(spanLength);
                    spanLength = 0;
                    continue;
                }
            }
            int minOffset = this.offsets.popMinimum();
            pos += minOffset;
            rest -= minOffset;
            spanLength = 0;
        }
    }

    public synchronized int spanBack(CharSequence s2, int length, UnicodeSet.SpanCondition spanCondition) {
        if (spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            return this.spanNotBack(s2, length);
        }
        int pos = this.spanSet.spanBack(s2, length, UnicodeSet.SpanCondition.CONTAINED);
        if (pos == 0) {
            return 0;
        }
        int spanLength = length - pos;
        int initSize = 0;
        if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
            initSize = this.maxLength16;
        }
        this.offsets.setMaxLength(initSize);
        int stringsLength = this.strings.size();
        int spanBackLengthsOffset = 0;
        if (this.all) {
            spanBackLengthsOffset = stringsLength;
        }
        while (true) {
            int i2;
            if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
                block1: for (i2 = 0; i2 < stringsLength; ++i2) {
                    int overlap = this.spanLengths[spanBackLengthsOffset + i2];
                    if (overlap == 255) continue;
                    String string = this.strings.get(i2);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                        int len1 = 0;
                        len1 = string.offsetByCodePoints(0, 1);
                        overlap -= len1;
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int dec = length16 - overlap; dec <= pos; ++dec) {
                        if (!this.offsets.containsOffset(dec) && UnicodeSetStringSpan.matches16CPB(s2, pos - dec, length, string, length16)) {
                            if (dec == pos) {
                                return 0;
                            }
                            this.offsets.addOffset(dec);
                        }
                        if (overlap == 0) continue block1;
                        --overlap;
                    }
                }
            } else {
                int maxDec = 0;
                int maxOverlap = 0;
                block3: for (i2 = 0; i2 < stringsLength; ++i2) {
                    int overlap = this.spanLengths[spanBackLengthsOffset + i2];
                    String string = this.strings.get(i2);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int dec = length16 - overlap; dec <= pos && overlap >= maxOverlap; --overlap, ++dec) {
                        if (overlap <= maxOverlap && dec <= maxDec || !UnicodeSetStringSpan.matches16CPB(s2, pos - dec, length, string, length16)) continue;
                        maxDec = dec;
                        maxOverlap = overlap;
                        continue block3;
                    }
                }
                if (maxDec != 0 || maxOverlap != 0) {
                    if ((pos -= maxDec) == 0) {
                        return 0;
                    }
                    spanLength = 0;
                    continue;
                }
            }
            if (spanLength != 0 || pos == length) {
                if (this.offsets.isEmpty()) {
                    return pos;
                }
            } else {
                if (this.offsets.isEmpty()) {
                    int oldPos = pos;
                    pos = this.spanSet.spanBack(s2, oldPos, UnicodeSet.SpanCondition.CONTAINED);
                    spanLength = oldPos - pos;
                    if (pos != 0 && spanLength != 0) continue;
                    return pos;
                }
                spanLength = UnicodeSetStringSpan.spanOneBack(this.spanSet, s2, pos);
                if (spanLength > 0) {
                    if (spanLength == pos) {
                        return 0;
                    }
                    pos -= spanLength;
                    this.offsets.shift(spanLength);
                    spanLength = 0;
                    continue;
                }
            }
            pos -= this.offsets.popMinimum();
            spanLength = 0;
        }
    }

    private int spanNot(CharSequence s2, int start, int length) {
        int cpLength;
        int pos = start;
        int rest = length;
        int stringsLength = this.strings.size();
        do {
            int i2;
            if ((i2 = this.spanNotSet.span(s2.subSequence(pos, pos + rest), UnicodeSet.SpanCondition.NOT_CONTAINED)) == rest) {
                return length;
            }
            cpLength = UnicodeSetStringSpan.spanOne(this.spanSet, s2, pos += i2, rest -= i2);
            if (cpLength > 0) {
                return pos - start;
            }
            for (i2 = 0; i2 < stringsLength; ++i2) {
                String string;
                int length16;
                if (this.spanLengths[i2] == 255 || (length16 = (string = this.strings.get(i2)).length()) > rest || !UnicodeSetStringSpan.matches16CPB(s2, pos, length, string, length16)) continue;
                return pos - start;
            }
            pos -= cpLength;
        } while ((rest += cpLength) != 0);
        return length;
    }

    private int spanNotBack(CharSequence s2, int length) {
        int cpLength;
        int pos = length;
        int stringsLength = this.strings.size();
        do {
            if ((pos = this.spanNotSet.spanBack(s2, pos, UnicodeSet.SpanCondition.NOT_CONTAINED)) == 0) {
                return 0;
            }
            cpLength = UnicodeSetStringSpan.spanOneBack(this.spanSet, s2, pos);
            if (cpLength > 0) {
                return pos;
            }
            for (int i2 = 0; i2 < stringsLength; ++i2) {
                String string;
                int length16;
                if (this.spanLengths[i2] == 255 || (length16 = (string = this.strings.get(i2)).length()) > pos || !UnicodeSetStringSpan.matches16CPB(s2, pos - length16, length, string, length16)) continue;
                return pos;
            }
        } while ((pos += cpLength) != 0);
        return 0;
    }

    static short makeSpanLengthByte(int spanLength) {
        return (short)(spanLength < 254 ? (int)spanLength : 254);
    }

    private static boolean matches16(CharSequence s2, int start, String t2, int length) {
        int end = start + length;
        while (length-- > 0) {
            if (s2.charAt(--end) == t2.charAt(length)) continue;
            return false;
        }
        return true;
    }

    static boolean matches16CPB(CharSequence s2, int start, int slength, String t2, int tlength) {
        return !(0 < start && com.ibm.icu.text.UTF16.isLeadSurrogate(s2.charAt(start - 1)) && com.ibm.icu.text.UTF16.isTrailSurrogate(s2.charAt(start + 0)) || tlength < slength && com.ibm.icu.text.UTF16.isLeadSurrogate(s2.charAt(start + tlength - 1)) && com.ibm.icu.text.UTF16.isTrailSurrogate(s2.charAt(start + tlength)) || !UnicodeSetStringSpan.matches16(s2, start, t2, tlength));
    }

    static int spanOne(UnicodeSet set, CharSequence s2, int start, int length) {
        char c2;
        char c3 = s2.charAt(start);
        if (c3 >= '\ud800' && c3 <= '\udbff' && length >= 2 && com.ibm.icu.text.UTF16.isTrailSurrogate(c2 = s2.charAt(start + 1))) {
            int supplementary = UCharacterProperty.getRawSupplementary(c3, c2);
            return set.contains(supplementary) ? 2 : -2;
        }
        return set.contains(c3) ? 1 : -1;
    }

    static int spanOneBack(UnicodeSet set, CharSequence s2, int length) {
        char c2;
        char c3 = s2.charAt(length - 1);
        if (c3 >= '\udc00' && c3 <= '\udfff' && length >= 2 && com.ibm.icu.text.UTF16.isLeadSurrogate(c2 = s2.charAt(length - 2))) {
            int supplementary = UCharacterProperty.getRawSupplementary(c2, c3);
            return set.contains(supplementary) ? 2 : -2;
        }
        return set.contains(c3) ? 1 : -1;
    }

    static class OffsetList {
        private boolean[] list = new boolean[16];
        private int length;
        private int start;

        public void setMaxLength(int maxLength) {
            if (maxLength > this.list.length) {
                this.list = new boolean[maxLength];
            }
            this.clear();
        }

        public void clear() {
            int i2 = this.list.length;
            while (i2-- > 0) {
                this.list[i2] = false;
            }
            this.length = 0;
            this.start = 0;
        }

        public boolean isEmpty() {
            return this.length == 0;
        }

        public void shift(int delta) {
            int i2 = this.start + delta;
            if (i2 >= this.list.length) {
                i2 -= this.list.length;
            }
            if (this.list[i2]) {
                this.list[i2] = false;
                --this.length;
            }
            this.start = i2;
        }

        public void addOffset(int offset) {
            int i2 = this.start + offset;
            if (i2 >= this.list.length) {
                i2 -= this.list.length;
            }
            this.list[i2] = true;
            ++this.length;
        }

        public boolean containsOffset(int offset) {
            int i2 = this.start + offset;
            if (i2 >= this.list.length) {
                i2 -= this.list.length;
            }
            return this.list[i2];
        }

        public int popMinimum() {
            int i2 = this.start;
            while (++i2 < this.list.length) {
                if (!this.list[i2]) continue;
                this.list[i2] = false;
                --this.length;
                int result = i2 - this.start;
                this.start = i2;
                return result;
            }
            int result = this.list.length - this.start;
            i2 = 0;
            while (!this.list[i2]) {
                ++i2;
            }
            this.list[i2] = false;
            --this.length;
            this.start = i2;
            return result + i2;
        }
    }
}

