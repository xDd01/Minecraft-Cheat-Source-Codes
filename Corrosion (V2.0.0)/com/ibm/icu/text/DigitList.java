/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import java.math.BigDecimal;
import java.math.BigInteger;

final class DigitList {
    public static final int MAX_LONG_DIGITS = 19;
    public static final int DBL_DIG = 17;
    public int decimalAt = 0;
    public int count = 0;
    public byte[] digits = new byte[19];
    private static byte[] LONG_MIN_REP;

    DigitList() {
    }

    private final void ensureCapacity(int digitCapacity, int digitsToCopy) {
        if (digitCapacity > this.digits.length) {
            byte[] newDigits = new byte[digitCapacity * 2];
            System.arraycopy(this.digits, 0, newDigits, 0, digitsToCopy);
            this.digits = newDigits;
        }
    }

    boolean isZero() {
        for (int i2 = 0; i2 < this.count; ++i2) {
            if (this.digits[i2] == 48) continue;
            return false;
        }
        return true;
    }

    public void append(int digit) {
        this.ensureCapacity(this.count + 1, this.count);
        this.digits[this.count++] = (byte)digit;
    }

    public byte getDigitValue(int i2) {
        return (byte)(this.digits[i2] - 48);
    }

    public final double getDouble() {
        if (this.count == 0) {
            return 0.0;
        }
        StringBuilder temp = new StringBuilder(this.count);
        temp.append('.');
        for (int i2 = 0; i2 < this.count; ++i2) {
            temp.append((char)this.digits[i2]);
        }
        temp.append('E');
        temp.append(Integer.toString(this.decimalAt));
        return Double.valueOf(temp.toString());
    }

    public final long getLong() {
        if (this.count == 0) {
            return 0L;
        }
        if (this.isLongMIN_VALUE()) {
            return Long.MIN_VALUE;
        }
        StringBuilder temp = new StringBuilder(this.count);
        for (int i2 = 0; i2 < this.decimalAt; ++i2) {
            temp.append(i2 < this.count ? (char)this.digits[i2] : (char)'0');
        }
        return Long.parseLong(temp.toString());
    }

    public BigInteger getBigInteger(boolean isPositive) {
        int i2;
        int len;
        if (this.isZero()) {
            return BigInteger.valueOf(0L);
        }
        int n2 = len = this.decimalAt > this.count ? this.decimalAt : this.count;
        if (!isPositive) {
            ++len;
        }
        char[] text = new char[len];
        int n3 = 0;
        if (!isPositive) {
            text[0] = 45;
            for (i2 = 0; i2 < this.count; ++i2) {
                text[i2 + 1] = (char)this.digits[i2];
            }
            n3 = this.count + 1;
        } else {
            for (i2 = 0; i2 < this.count; ++i2) {
                text[i2] = (char)this.digits[i2];
            }
            n3 = this.count;
        }
        for (i2 = n3; i2 < text.length; ++i2) {
            text[i2] = 48;
        }
        return new BigInteger(new String(text));
    }

    private String getStringRep(boolean isPositive) {
        int d2;
        if (this.isZero()) {
            return "0";
        }
        StringBuilder stringRep = new StringBuilder(this.count + 1);
        if (!isPositive) {
            stringRep.append('-');
        }
        if ((d2 = this.decimalAt) < 0) {
            stringRep.append('.');
            while (d2 < 0) {
                stringRep.append('0');
                ++d2;
            }
            d2 = -1;
        }
        for (int i2 = 0; i2 < this.count; ++i2) {
            if (d2 == i2) {
                stringRep.append('.');
            }
            stringRep.append((char)this.digits[i2]);
        }
        while (d2-- > this.count) {
            stringRep.append('0');
        }
        return stringRep.toString();
    }

    public BigDecimal getBigDecimal(boolean isPositive) {
        if (this.isZero()) {
            return BigDecimal.valueOf(0L);
        }
        long scale = (long)this.count - (long)this.decimalAt;
        if (scale > 0L) {
            int numDigits = this.count;
            if (scale > Integer.MAX_VALUE) {
                long numShift = scale - Integer.MAX_VALUE;
                if (numShift < (long)this.count) {
                    numDigits = (int)((long)numDigits - numShift);
                } else {
                    return new BigDecimal(0);
                }
            }
            StringBuilder significantDigits = new StringBuilder(numDigits + 1);
            if (!isPositive) {
                significantDigits.append('-');
            }
            for (int i2 = 0; i2 < numDigits; ++i2) {
                significantDigits.append((char)this.digits[i2]);
            }
            BigInteger unscaledVal = new BigInteger(significantDigits.toString());
            return new BigDecimal(unscaledVal, (int)scale);
        }
        return new BigDecimal(this.getStringRep(isPositive));
    }

    public com.ibm.icu.math.BigDecimal getBigDecimalICU(boolean isPositive) {
        if (this.isZero()) {
            return com.ibm.icu.math.BigDecimal.valueOf(0L);
        }
        long scale = (long)this.count - (long)this.decimalAt;
        if (scale > 0L) {
            int numDigits = this.count;
            if (scale > Integer.MAX_VALUE) {
                long numShift = scale - Integer.MAX_VALUE;
                if (numShift < (long)this.count) {
                    numDigits = (int)((long)numDigits - numShift);
                } else {
                    return new com.ibm.icu.math.BigDecimal(0);
                }
            }
            StringBuilder significantDigits = new StringBuilder(numDigits + 1);
            if (!isPositive) {
                significantDigits.append('-');
            }
            for (int i2 = 0; i2 < numDigits; ++i2) {
                significantDigits.append((char)this.digits[i2]);
            }
            BigInteger unscaledVal = new BigInteger(significantDigits.toString());
            return new com.ibm.icu.math.BigDecimal(unscaledVal, (int)scale);
        }
        return new com.ibm.icu.math.BigDecimal(this.getStringRep(isPositive));
    }

    boolean isIntegral() {
        while (this.count > 0 && this.digits[this.count - 1] == 48) {
            --this.count;
        }
        return this.count == 0 || this.decimalAt >= this.count;
    }

    final void set(double source, int maximumDigits, boolean fixedPoint) {
        if (source == 0.0) {
            source = 0.0;
        }
        String rep = Double.toString(source);
        this.set(rep, 19);
        if (fixedPoint) {
            if (-this.decimalAt > maximumDigits) {
                this.count = 0;
                return;
            }
            if (-this.decimalAt == maximumDigits) {
                if (this.shouldRoundUp(0)) {
                    this.count = 1;
                    ++this.decimalAt;
                    this.digits[0] = 49;
                } else {
                    this.count = 0;
                }
                return;
            }
        }
        while (this.count > 1 && this.digits[this.count - 1] == 48) {
            --this.count;
        }
        this.round(fixedPoint ? maximumDigits + this.decimalAt : (maximumDigits == 0 ? -1 : maximumDigits));
    }

    private void set(String rep, int maxCount) {
        this.decimalAt = -1;
        this.count = 0;
        int exponent = 0;
        int leadingZerosAfterDecimal = 0;
        boolean nonZeroDigitSeen = false;
        int i2 = 0;
        if (rep.charAt(i2) == '-') {
            ++i2;
        }
        while (i2 < rep.length()) {
            char c2 = rep.charAt(i2);
            if (c2 == '.') {
                this.decimalAt = this.count;
            } else {
                if (c2 == 'e' || c2 == 'E') {
                    if (rep.charAt(++i2) == '+') {
                        ++i2;
                    }
                    exponent = Integer.valueOf(rep.substring(i2));
                    break;
                }
                if (this.count < maxCount) {
                    if (!nonZeroDigitSeen) {
                        boolean bl2 = nonZeroDigitSeen = c2 != '0';
                        if (!nonZeroDigitSeen && this.decimalAt != -1) {
                            ++leadingZerosAfterDecimal;
                        }
                    }
                    if (nonZeroDigitSeen) {
                        this.ensureCapacity(this.count + 1, this.count);
                        this.digits[this.count++] = (byte)c2;
                    }
                }
            }
            ++i2;
        }
        if (this.decimalAt == -1) {
            this.decimalAt = this.count;
        }
        this.decimalAt += exponent - leadingZerosAfterDecimal;
    }

    private boolean shouldRoundUp(int maximumDigits) {
        if (maximumDigits < this.count) {
            if (this.digits[maximumDigits] > 53) {
                return true;
            }
            if (this.digits[maximumDigits] == 53) {
                for (int i2 = maximumDigits + 1; i2 < this.count; ++i2) {
                    if (this.digits[i2] == 48) continue;
                    return true;
                }
                return maximumDigits > 0 && this.digits[maximumDigits - 1] % 2 != 0;
            }
        }
        return false;
    }

    public final void round(int maximumDigits) {
        if (maximumDigits >= 0 && maximumDigits < this.count) {
            if (this.shouldRoundUp(maximumDigits)) {
                do {
                    if (--maximumDigits < 0) {
                        this.digits[0] = 49;
                        ++this.decimalAt;
                        maximumDigits = 0;
                        break;
                    }
                    int n2 = maximumDigits;
                    this.digits[n2] = (byte)(this.digits[n2] + 1);
                } while (this.digits[maximumDigits] > 57);
                ++maximumDigits;
            }
            this.count = maximumDigits;
        }
        while (this.count > 1 && this.digits[this.count - 1] == 48) {
            --this.count;
        }
    }

    public final void set(long source) {
        this.set(source, 0);
    }

    public final void set(long source, int maximumDigits) {
        if (source <= 0L) {
            if (source == Long.MIN_VALUE) {
                this.count = 19;
                this.decimalAt = 19;
                System.arraycopy(LONG_MIN_REP, 0, this.digits, 0, this.count);
            } else {
                this.count = 0;
                this.decimalAt = 0;
            }
        } else {
            int left = 19;
            while (source > 0L) {
                this.digits[--left] = (byte)(48L + source % 10L);
                source /= 10L;
            }
            this.decimalAt = 19 - left;
            int right = 18;
            while (this.digits[right] == 48) {
                --right;
            }
            this.count = right - left + 1;
            System.arraycopy(this.digits, left, this.digits, 0, this.count);
        }
        if (maximumDigits > 0) {
            this.round(maximumDigits);
        }
    }

    public final void set(BigInteger source, int maximumDigits) {
        String stringDigits = source.toString();
        this.count = this.decimalAt = stringDigits.length();
        while (this.count > 1 && stringDigits.charAt(this.count - 1) == '0') {
            --this.count;
        }
        int offset = 0;
        if (stringDigits.charAt(0) == '-') {
            ++offset;
            --this.count;
            --this.decimalAt;
        }
        this.ensureCapacity(this.count, 0);
        for (int i2 = 0; i2 < this.count; ++i2) {
            this.digits[i2] = (byte)stringDigits.charAt(i2 + offset);
        }
        if (maximumDigits > 0) {
            this.round(maximumDigits);
        }
    }

    private void setBigDecimalDigits(String stringDigits, int maximumDigits, boolean fixedPoint) {
        this.set(stringDigits, stringDigits.length());
        this.round(fixedPoint ? maximumDigits + this.decimalAt : (maximumDigits == 0 ? -1 : maximumDigits));
    }

    public final void set(BigDecimal source, int maximumDigits, boolean fixedPoint) {
        this.setBigDecimalDigits(source.toString(), maximumDigits, fixedPoint);
    }

    public final void set(com.ibm.icu.math.BigDecimal source, int maximumDigits, boolean fixedPoint) {
        this.setBigDecimalDigits(source.toString(), maximumDigits, fixedPoint);
    }

    private boolean isLongMIN_VALUE() {
        if (this.decimalAt != this.count || this.count != 19) {
            return false;
        }
        for (int i2 = 0; i2 < this.count; ++i2) {
            if (this.digits[i2] == LONG_MIN_REP[i2]) continue;
            return false;
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DigitList)) {
            return false;
        }
        DigitList other = (DigitList)obj;
        if (this.count != other.count || this.decimalAt != other.decimalAt) {
            return false;
        }
        for (int i2 = 0; i2 < this.count; ++i2) {
            if (this.digits[i2] == other.digits[i2]) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashcode = this.decimalAt;
        for (int i2 = 0; i2 < this.count; ++i2) {
            hashcode = hashcode * 37 + this.digits[i2];
        }
        return hashcode;
    }

    public String toString() {
        if (this.isZero()) {
            return "0";
        }
        StringBuilder buf = new StringBuilder("0.");
        for (int i2 = 0; i2 < this.count; ++i2) {
            buf.append((char)this.digits[i2]);
        }
        buf.append("x10^");
        buf.append(this.decimalAt);
        return buf.toString();
    }

    static {
        String s2 = Long.toString(Long.MIN_VALUE);
        LONG_MIN_REP = new byte[19];
        for (int i2 = 0; i2 < 19; ++i2) {
            DigitList.LONG_MIN_REP[i2] = (byte)s2.charAt(i2 + 1);
        }
    }
}

