/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.Utility;

public class ImplicitCEGenerator {
    static final boolean DEBUG = false;
    static final long topByte = 0xFF000000L;
    static final long bottomByte = 255L;
    static final long fourBytes = 0xFFFFFFFFL;
    static final int MAX_INPUT = 0x220001;
    public static final int CJK_BASE = 19968;
    public static final int CJK_LIMIT = 40909;
    public static final int CJK_COMPAT_USED_BASE = 64014;
    public static final int CJK_COMPAT_USED_LIMIT = 64048;
    public static final int CJK_A_BASE = 13312;
    public static final int CJK_A_LIMIT = 19894;
    public static final int CJK_B_BASE = 131072;
    public static final int CJK_B_LIMIT = 173783;
    public static final int CJK_C_BASE = 173824;
    public static final int CJK_C_LIMIT = 177973;
    public static final int CJK_D_BASE = 177984;
    public static final int CJK_D_LIMIT = 178206;
    int final3Multiplier;
    int final4Multiplier;
    int final3Count;
    int final4Count;
    int medialCount;
    int min3Primary;
    int min4Primary;
    int max4Primary;
    int minTrail;
    int maxTrail;
    int max3Trail;
    int max4Trail;
    int min4Boundary;
    static int NON_CJK_OFFSET = 0x110000;

    public int getGap4() {
        return this.final4Multiplier - 1;
    }

    public int getGap3() {
        return this.final3Multiplier - 1;
    }

    public ImplicitCEGenerator(int minPrimary, int maxPrimary) {
        this(minPrimary, maxPrimary, 4, 254, 1, 1);
    }

    public ImplicitCEGenerator(int minPrimary, int maxPrimary, int minTrail, int maxTrail, int gap3, int primaries3count) {
        if (minPrimary < 0 || minPrimary >= maxPrimary || maxPrimary > 255) {
            throw new IllegalArgumentException("bad lead bytes");
        }
        if (minTrail < 0 || minTrail >= maxTrail || maxTrail > 255) {
            throw new IllegalArgumentException("bad trail bytes");
        }
        if (primaries3count < 1) {
            throw new IllegalArgumentException("bad three-byte primaries");
        }
        this.minTrail = minTrail;
        this.maxTrail = maxTrail;
        this.min3Primary = minPrimary;
        this.max4Primary = maxPrimary;
        this.final3Multiplier = gap3 + 1;
        this.final3Count = (maxTrail - minTrail + 1) / this.final3Multiplier;
        this.max3Trail = minTrail + (this.final3Count - 1) * this.final3Multiplier;
        this.medialCount = maxTrail - minTrail + 1;
        int threeByteCount = this.medialCount * this.final3Count;
        int primariesAvailable = maxPrimary - minPrimary + 1;
        int primaries4count = primariesAvailable - primaries3count;
        int min3ByteCoverage = primaries3count * threeByteCount;
        this.min4Primary = minPrimary + primaries3count;
        this.min4Boundary = min3ByteCoverage;
        int totalNeeded = 0x220001 - this.min4Boundary;
        int neededPerPrimaryByte = ImplicitCEGenerator.divideAndRoundUp(totalNeeded, primaries4count);
        int neededPerFinalByte = ImplicitCEGenerator.divideAndRoundUp(neededPerPrimaryByte, this.medialCount * this.medialCount);
        int gap4 = (maxTrail - minTrail - 1) / neededPerFinalByte;
        if (gap4 < 1) {
            throw new IllegalArgumentException("must have larger gap4s");
        }
        this.final4Multiplier = gap4 + 1;
        this.final4Count = neededPerFinalByte;
        this.max4Trail = minTrail + (this.final4Count - 1) * this.final4Multiplier;
        if (primaries4count * this.medialCount * this.medialCount * this.final4Count < 0x220001) {
            throw new IllegalArgumentException("internal error");
        }
    }

    public static int divideAndRoundUp(int a2, int b2) {
        return 1 + (a2 - 1) / b2;
    }

    public int getRawFromImplicit(int implicit) {
        int result;
        int b3 = implicit & 0xFF;
        int b2 = (implicit >>= 8) & 0xFF;
        int b1 = (implicit >>= 8) & 0xFF;
        int b0 = (implicit >>= 8) & 0xFF;
        if (b0 < this.min3Primary || b0 > this.max4Primary || b1 < this.minTrail || b1 > this.maxTrail) {
            return -1;
        }
        b1 -= this.minTrail;
        if (b0 < this.min4Primary) {
            if (b2 < this.minTrail || b2 > this.max3Trail || b3 != 0) {
                return -1;
            }
            int remainder = (b2 -= this.minTrail) % this.final3Multiplier;
            if (remainder != 0) {
                return -1;
            }
            result = ((b0 -= this.min3Primary) * this.medialCount + b1) * this.final3Count + (b2 /= this.final3Multiplier);
        } else {
            if (b2 < this.minTrail || b2 > this.maxTrail || b3 < this.minTrail || b3 > this.max4Trail) {
                return -1;
            }
            b2 -= this.minTrail;
            int remainder = (b3 -= this.minTrail) % this.final4Multiplier;
            if (remainder != 0) {
                return -1;
            }
            result = (((b0 -= this.min4Primary) * this.medialCount + b1) * this.medialCount + b2) * this.final4Count + (b3 /= this.final4Multiplier) + this.min4Boundary;
        }
        if (result < 0 || result > 0x220001) {
            return -1;
        }
        return result;
    }

    public int getImplicitFromRaw(int cp2) {
        if (cp2 < 0 || cp2 > 0x220001) {
            throw new IllegalArgumentException("Code point out of range " + Utility.hex(cp2));
        }
        int last0 = cp2 - this.min4Boundary;
        if (last0 < 0) {
            int last1 = cp2 / this.final3Count;
            last0 = cp2 % this.final3Count;
            int last2 = last1 / this.medialCount;
            last1 %= this.medialCount;
            last0 = this.minTrail + last0 * this.final3Multiplier;
            last1 = this.minTrail + last1;
            if ((last2 = this.min3Primary + last2) >= this.min4Primary) {
                throw new IllegalArgumentException("4-byte out of range: " + Utility.hex(cp2) + ", " + Utility.hex(last2));
            }
            return (last2 << 24) + (last1 << 16) + (last0 << 8);
        }
        int last1 = last0 / this.final4Count;
        last0 %= this.final4Count;
        int last2 = last1 / this.medialCount;
        last1 %= this.medialCount;
        int last3 = last2 / this.medialCount;
        last2 %= this.medialCount;
        last0 = this.minTrail + last0 * this.final4Multiplier;
        last1 = this.minTrail + last1;
        last2 = this.minTrail + last2;
        if ((last3 = this.min4Primary + last3) > this.max4Primary) {
            throw new IllegalArgumentException("4-byte out of range: " + Utility.hex(cp2) + ", " + Utility.hex(last3));
        }
        return (last3 << 24) + (last2 << 16) + (last1 << 8) + last0;
    }

    public int getImplicitFromCodePoint(int cp2) {
        cp2 = ImplicitCEGenerator.swapCJK(cp2) + 1;
        return this.getImplicitFromRaw(cp2);
    }

    public static int swapCJK(int i2) {
        if (i2 >= 19968) {
            if (i2 < 40909) {
                return i2 - 19968;
            }
            if (i2 < 64014) {
                return i2 + NON_CJK_OFFSET;
            }
            if (i2 < 64048) {
                return i2 - 64014 + 20941;
            }
            if (i2 < 131072) {
                return i2 + NON_CJK_OFFSET;
            }
            if (i2 < 173783) {
                return i2;
            }
            if (i2 < 173824) {
                return i2 + NON_CJK_OFFSET;
            }
            if (i2 < 177973) {
                return i2;
            }
            if (i2 < 177984) {
                return i2 + NON_CJK_OFFSET;
            }
            if (i2 < 178206) {
                return i2;
            }
            return i2 + NON_CJK_OFFSET;
        }
        if (i2 < 13312) {
            return i2 + NON_CJK_OFFSET;
        }
        if (i2 < 19894) {
            return i2 - 13312 + 20941 + 34;
        }
        return i2 + NON_CJK_OFFSET;
    }

    public int getMinTrail() {
        return this.minTrail;
    }

    public int getMaxTrail() {
        return this.maxTrail;
    }

    public int getCodePointFromRaw(int i2) {
        int result = 0;
        result = --i2 >= NON_CJK_OFFSET ? i2 - NON_CJK_OFFSET : (i2 >= 131072 ? i2 : (i2 < 40869 ? (i2 < 20941 ? i2 + 19968 : (i2 < 20975 ? i2 + 64014 - 20941 : i2 + 13312 - 20941 - 34)) : -1));
        return result;
    }

    public int getRawFromCodePoint(int i2) {
        return ImplicitCEGenerator.swapCJK(i2) + 1;
    }
}

