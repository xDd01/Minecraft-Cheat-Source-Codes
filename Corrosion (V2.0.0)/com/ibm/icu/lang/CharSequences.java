/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.lang;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CharSequences {
    public static int matchAfter(CharSequence a2, CharSequence b2, int aIndex, int bIndex) {
        char cb2;
        char ca2;
        int j2;
        int i2 = aIndex;
        int alen = a2.length();
        int blen = b2.length();
        for (j2 = bIndex; i2 < alen && j2 < blen && (ca2 = a2.charAt(i2)) == (cb2 = b2.charAt(j2)); ++i2, ++j2) {
        }
        int result = i2 - aIndex;
        if (result != 0 && !CharSequences.onCharacterBoundary(a2, i2) && !CharSequences.onCharacterBoundary(b2, j2)) {
            --result;
        }
        return result;
    }

    public int codePointLength(CharSequence s2) {
        return Character.codePointCount(s2, 0, s2.length());
    }

    public static final boolean equals(int codepoint, CharSequence other) {
        if (other == null) {
            return false;
        }
        switch (other.length()) {
            case 1: {
                return codepoint == other.charAt(0);
            }
            case 2: {
                return codepoint > 65535 && codepoint == Character.codePointAt(other, 0);
            }
        }
        return false;
    }

    public static final boolean equals(CharSequence other, int codepoint) {
        return CharSequences.equals(codepoint, other);
    }

    public static int compare(CharSequence string, int codePoint) {
        if (codePoint < 0 || codePoint > 0x10FFFF) {
            throw new IllegalArgumentException();
        }
        int stringLength = string.length();
        if (stringLength == 0) {
            return -1;
        }
        char firstChar = string.charAt(0);
        int offset = codePoint - 65536;
        if (offset < 0) {
            int result = firstChar - codePoint;
            if (result != 0) {
                return result;
            }
            return stringLength - 1;
        }
        char lead = (char)((offset >>> 10) + 55296);
        int result = firstChar - lead;
        if (result != 0) {
            return result;
        }
        if (stringLength > 1) {
            char trail = (char)((offset & 0x3FF) + 56320);
            result = string.charAt(1) - trail;
            if (result != 0) {
                return result;
            }
        }
        return stringLength - 2;
    }

    public static int compare(int codepoint, CharSequence a2) {
        return -CharSequences.compare(a2, codepoint);
    }

    public static int getSingleCodePoint(CharSequence s2) {
        int length = s2.length();
        if (length < 1 || length > 2) {
            return Integer.MAX_VALUE;
        }
        int result = Character.codePointAt(s2, 0);
        return result < 65536 == (length == 1) ? result : Integer.MAX_VALUE;
    }

    public static final <T> boolean equals(T a2, T b2) {
        return a2 == null ? b2 == null : (b2 == null ? false : a2.equals(b2));
    }

    public static int compare(CharSequence a2, CharSequence b2) {
        int blength;
        int alength = a2.length();
        int min = alength <= (blength = b2.length()) ? alength : blength;
        for (int i2 = 0; i2 < min; ++i2) {
            int diff = a2.charAt(i2) - b2.charAt(i2);
            if (diff == 0) continue;
            return diff;
        }
        return alength - blength;
    }

    public static boolean equalsChars(CharSequence a2, CharSequence b2) {
        return a2.length() == b2.length() && CharSequences.compare(a2, b2) == 0;
    }

    public static boolean onCharacterBoundary(CharSequence s2, int i2) {
        return i2 <= 0 || i2 >= s2.length() || !Character.isHighSurrogate(s2.charAt(i2 - 1)) || !Character.isLowSurrogate(s2.charAt(i2));
    }

    public static int indexOf(CharSequence s2, int codePoint) {
        int cp2;
        for (int i2 = 0; i2 < s2.length(); i2 += Character.charCount(cp2)) {
            cp2 = Character.codePointAt(s2, i2);
            if (cp2 != codePoint) continue;
            return i2;
        }
        return -1;
    }

    public static int[] codePoints(CharSequence s2) {
        int[] result = new int[s2.length()];
        int j2 = 0;
        for (int i2 = 0; i2 < s2.length(); ++i2) {
            char last;
            char cp2 = s2.charAt(i2);
            if (cp2 >= '\udc00' && cp2 <= '\udfff' && i2 != 0 && (last = (char)result[j2 - 1]) >= '\ud800' && last <= '\udbff') {
                result[j2 - 1] = Character.toCodePoint(last, cp2);
                continue;
            }
            result[j2++] = cp2;
        }
        if (j2 == result.length) {
            return result;
        }
        int[] shortResult = new int[j2];
        System.arraycopy(result, 0, shortResult, 0, j2);
        return shortResult;
    }

    private CharSequences() {
    }
}

