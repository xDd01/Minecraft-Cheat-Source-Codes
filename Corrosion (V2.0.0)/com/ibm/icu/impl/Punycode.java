/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.StringPrepParseException;
import com.ibm.icu.text.UTF16;

public final class Punycode {
    private static final int BASE = 36;
    private static final int TMIN = 1;
    private static final int TMAX = 26;
    private static final int SKEW = 38;
    private static final int DAMP = 700;
    private static final int INITIAL_BIAS = 72;
    private static final int INITIAL_N = 128;
    private static final int HYPHEN = 45;
    private static final int DELIMITER = 45;
    private static final int ZERO = 48;
    private static final int SMALL_A = 97;
    private static final int SMALL_Z = 122;
    private static final int CAPITAL_A = 65;
    private static final int CAPITAL_Z = 90;
    private static final int MAX_CP_COUNT = 200;
    static final int[] basicToDigit = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    private static int adaptBias(int delta, int length, boolean firstTime) {
        delta = firstTime ? (delta /= 700) : (delta /= 2);
        delta += delta / length;
        int count = 0;
        while (delta > 455) {
            delta /= 35;
            count += 36;
        }
        return count + 36 * delta / (delta + 38);
    }

    private static char asciiCaseMap(char b2, boolean uppercase) {
        if (uppercase) {
            if ('a' <= b2 && b2 <= 'z') {
                b2 = (char)(b2 - 32);
            }
        } else if ('A' <= b2 && b2 <= 'Z') {
            b2 = (char)(b2 + 32);
        }
        return b2;
    }

    private static char digitToBasic(int digit, boolean uppercase) {
        if (digit < 26) {
            if (uppercase) {
                return (char)(65 + digit);
            }
            return (char)(97 + digit);
        }
        return (char)(22 + digit);
    }

    public static StringBuilder encode(CharSequence src, boolean[] caseFlags) throws StringPrepParseException {
        int n2;
        int j2;
        int[] cpBuffer = new int[200];
        int srcLength = src.length();
        int destCapacity = 200;
        char[] dest = new char[destCapacity];
        StringBuilder result = new StringBuilder();
        int destLength = 0;
        int srcCPCount = 0;
        for (j2 = 0; j2 < srcLength; ++j2) {
            char c2;
            if (srcCPCount == 200) {
                throw new IndexOutOfBoundsException();
            }
            char c3 = src.charAt(j2);
            if (Punycode.isBasic(c3)) {
                if (destLength < destCapacity) {
                    cpBuffer[srcCPCount++] = 0;
                    dest[destLength] = caseFlags != null ? Punycode.asciiCaseMap(c3, caseFlags[j2]) : c3;
                }
                ++destLength;
                continue;
            }
            n2 = (caseFlags != null && caseFlags[j2] ? 1 : 0) << 31;
            if (!UTF16.isSurrogate(c3)) {
                n2 |= c3;
            } else if (UTF16.isLeadSurrogate(c3) && j2 + 1 < srcLength && UTF16.isTrailSurrogate(c2 = src.charAt(j2 + 1))) {
                ++j2;
                n2 |= UCharacter.getCodePoint(c3, c2);
            } else {
                throw new StringPrepParseException("Illegal char found", 1);
            }
            cpBuffer[srcCPCount++] = n2;
        }
        int basicLength = destLength;
        if (basicLength > 0) {
            if (destLength < destCapacity) {
                dest[destLength] = 45;
            }
            ++destLength;
        }
        n2 = 128;
        int delta = 0;
        int bias = 72;
        int handledCPCount = basicLength;
        while (handledCPCount < srcCPCount) {
            int q2;
            int m2 = Integer.MAX_VALUE;
            for (j2 = 0; j2 < srcCPCount; ++j2) {
                q2 = cpBuffer[j2] & Integer.MAX_VALUE;
                if (n2 > q2 || q2 >= m2) continue;
                m2 = q2;
            }
            if (m2 - n2 > (0x7FFFFF37 - delta) / (handledCPCount + 1)) {
                throw new IllegalStateException("Internal program error");
            }
            delta += (m2 - n2) * (handledCPCount + 1);
            n2 = m2;
            for (j2 = 0; j2 < srcCPCount; ++j2) {
                q2 = cpBuffer[j2] & Integer.MAX_VALUE;
                if (q2 < n2) {
                    ++delta;
                    continue;
                }
                if (q2 != n2) continue;
                q2 = delta;
                int k2 = 36;
                while (true) {
                    int t2;
                    if ((t2 = k2 - bias) < 1) {
                        t2 = 1;
                    } else if (k2 >= bias + 26) {
                        t2 = 26;
                    }
                    if (q2 < t2) break;
                    if (destLength < destCapacity) {
                        dest[destLength++] = Punycode.digitToBasic(t2 + (q2 - t2) % (36 - t2), false);
                    }
                    q2 = (q2 - t2) / (36 - t2);
                    k2 += 36;
                }
                if (destLength < destCapacity) {
                    dest[destLength++] = Punycode.digitToBasic(q2, cpBuffer[j2] < 0);
                }
                bias = Punycode.adaptBias(delta, handledCPCount + 1, handledCPCount == basicLength);
                delta = 0;
                ++handledCPCount;
            }
            ++delta;
            ++n2;
        }
        return result.append(dest, 0, destLength);
    }

    private static boolean isBasic(int ch) {
        return ch < 128;
    }

    private static boolean isBasicUpperCase(int ch) {
        return 65 <= ch && ch >= 90;
    }

    private static boolean isSurrogate(int ch) {
        return (ch & 0xFFFFF800) == 55296;
    }

    public static StringBuilder decode(CharSequence src, boolean[] caseFlags) throws StringPrepParseException {
        int in2;
        int destCPCount;
        int srcLength = src.length();
        StringBuilder result = new StringBuilder();
        int destCapacity = 200;
        char[] dest = new char[destCapacity];
        int j2 = srcLength;
        while (j2 > 0 && src.charAt(--j2) != '-') {
        }
        int basicLength = destCPCount = j2;
        int destLength = destCPCount;
        while (j2 > 0) {
            char b2;
            if (!Punycode.isBasic(b2 = src.charAt(--j2))) {
                throw new StringPrepParseException("Illegal char found", 0);
            }
            if (j2 >= destCapacity) continue;
            dest[j2] = b2;
            if (caseFlags == null) continue;
            caseFlags[j2] = Punycode.isBasicUpperCase(b2);
        }
        int n2 = 128;
        int i2 = 0;
        int bias = 72;
        int firstSupplementaryIndex = 1000000000;
        int n3 = in2 = basicLength > 0 ? basicLength + 1 : 0;
        while (in2 < srcLength) {
            int oldi = i2;
            int w2 = 1;
            int k2 = 36;
            while (true) {
                int digit;
                if (in2 >= srcLength) {
                    throw new StringPrepParseException("Illegal char found", 1);
                }
                if ((digit = basicToDigit[src.charAt(in2++) & 0xFF]) < 0) {
                    throw new StringPrepParseException("Invalid char found", 0);
                }
                if (digit > (Integer.MAX_VALUE - i2) / w2) {
                    throw new StringPrepParseException("Illegal char found", 1);
                }
                i2 += digit * w2;
                int t2 = k2 - bias;
                if (t2 < 1) {
                    t2 = 1;
                } else if (k2 >= bias + 26) {
                    t2 = 26;
                }
                if (digit < t2) break;
                if (w2 > Integer.MAX_VALUE / (36 - t2)) {
                    throw new StringPrepParseException("Illegal char found", 1);
                }
                w2 *= 36 - t2;
                k2 += 36;
            }
            bias = Punycode.adaptBias(i2 - oldi, ++destCPCount, oldi == 0);
            if (i2 / destCPCount > Integer.MAX_VALUE - n2) {
                throw new StringPrepParseException("Illegal char found", 1);
            }
            n2 += i2 / destCPCount;
            i2 %= destCPCount;
            if (n2 > 0x10FFFF || Punycode.isSurrogate(n2)) {
                throw new StringPrepParseException("Illegal char found", 1);
            }
            int cpLength = UTF16.getCharCount(n2);
            if (destLength + cpLength < destCapacity) {
                int codeUnitIndex;
                if (i2 <= firstSupplementaryIndex) {
                    codeUnitIndex = i2;
                    firstSupplementaryIndex = cpLength > 1 ? codeUnitIndex : ++firstSupplementaryIndex;
                } else {
                    codeUnitIndex = firstSupplementaryIndex;
                    codeUnitIndex = UTF16.moveCodePointOffset(dest, 0, destLength, codeUnitIndex, i2 - codeUnitIndex);
                }
                if (codeUnitIndex < destLength) {
                    System.arraycopy(dest, codeUnitIndex, dest, codeUnitIndex + cpLength, destLength - codeUnitIndex);
                    if (caseFlags != null) {
                        System.arraycopy(caseFlags, codeUnitIndex, caseFlags, codeUnitIndex + cpLength, destLength - codeUnitIndex);
                    }
                }
                if (cpLength == 1) {
                    dest[codeUnitIndex] = (char)n2;
                } else {
                    dest[codeUnitIndex] = UTF16.getLeadSurrogate(n2);
                    dest[codeUnitIndex + 1] = UTF16.getTrailSurrogate(n2);
                }
                if (caseFlags != null) {
                    caseFlags[codeUnitIndex] = Punycode.isBasicUpperCase(src.charAt(in2 - 1));
                    if (cpLength == 2) {
                        caseFlags[codeUnitIndex + 1] = false;
                    }
                }
            }
            destLength += cpLength;
            ++i2;
        }
        result.append(dest, 0, destLength);
        return result;
    }
}

