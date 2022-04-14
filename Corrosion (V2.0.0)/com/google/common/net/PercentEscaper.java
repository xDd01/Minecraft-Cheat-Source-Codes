/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.UnicodeEscaper;

@Beta
@GwtCompatible
public final class PercentEscaper
extends UnicodeEscaper {
    private static final char[] PLUS_SIGN = new char[]{'+'};
    private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private final boolean plusForSpace;
    private final boolean[] safeOctets;

    public PercentEscaper(String safeChars, boolean plusForSpace) {
        Preconditions.checkNotNull(safeChars);
        if (safeChars.matches(".*[0-9A-Za-z].*")) {
            throw new IllegalArgumentException("Alphanumeric characters are always 'safe' and should not be explicitly specified");
        }
        safeChars = safeChars + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        if (plusForSpace && safeChars.contains(" ")) {
            throw new IllegalArgumentException("plusForSpace cannot be specified when space is a 'safe' character");
        }
        this.plusForSpace = plusForSpace;
        this.safeOctets = PercentEscaper.createSafeOctets(safeChars);
    }

    private static boolean[] createSafeOctets(String safeChars) {
        char[] safeCharArray;
        int maxChar = -1;
        for (char c2 : safeCharArray = safeChars.toCharArray()) {
            maxChar = Math.max(c2, maxChar);
        }
        boolean[] octets = new boolean[maxChar + 1];
        for (char c3 : safeCharArray) {
            octets[c3] = true;
        }
        return octets;
    }

    @Override
    protected int nextEscapeIndex(CharSequence csq, int index, int end) {
        char c2;
        Preconditions.checkNotNull(csq);
        while (index < end && (c2 = csq.charAt(index)) < this.safeOctets.length && this.safeOctets[c2]) {
            ++index;
        }
        return index;
    }

    @Override
    public String escape(String s2) {
        Preconditions.checkNotNull(s2);
        int slen = s2.length();
        for (int index = 0; index < slen; ++index) {
            char c2 = s2.charAt(index);
            if (c2 < this.safeOctets.length && this.safeOctets[c2]) continue;
            return this.escapeSlow(s2, index);
        }
        return s2;
    }

    @Override
    protected char[] escape(int cp2) {
        if (cp2 < this.safeOctets.length && this.safeOctets[cp2]) {
            return null;
        }
        if (cp2 == 32 && this.plusForSpace) {
            return PLUS_SIGN;
        }
        if (cp2 <= 127) {
            char[] dest = new char[3];
            dest[0] = 37;
            dest[2] = UPPER_HEX_DIGITS[cp2 & 0xF];
            dest[1] = UPPER_HEX_DIGITS[cp2 >>> 4];
            return dest;
        }
        if (cp2 <= 2047) {
            char[] dest = new char[6];
            dest[0] = 37;
            dest[3] = 37;
            dest[5] = UPPER_HEX_DIGITS[cp2 & 0xF];
            dest[4] = UPPER_HEX_DIGITS[8 | (cp2 >>>= 4) & 3];
            dest[2] = UPPER_HEX_DIGITS[(cp2 >>>= 2) & 0xF];
            dest[1] = UPPER_HEX_DIGITS[0xC | (cp2 >>>= 4)];
            return dest;
        }
        if (cp2 <= 65535) {
            char[] dest = new char[9];
            dest[0] = 37;
            dest[1] = 69;
            dest[3] = 37;
            dest[6] = 37;
            dest[8] = UPPER_HEX_DIGITS[cp2 & 0xF];
            dest[7] = UPPER_HEX_DIGITS[8 | (cp2 >>>= 4) & 3];
            dest[5] = UPPER_HEX_DIGITS[(cp2 >>>= 2) & 0xF];
            dest[4] = UPPER_HEX_DIGITS[8 | (cp2 >>>= 4) & 3];
            dest[2] = UPPER_HEX_DIGITS[cp2 >>>= 2];
            return dest;
        }
        if (cp2 <= 0x10FFFF) {
            char[] dest = new char[12];
            dest[0] = 37;
            dest[1] = 70;
            dest[3] = 37;
            dest[6] = 37;
            dest[9] = 37;
            dest[11] = UPPER_HEX_DIGITS[cp2 & 0xF];
            dest[10] = UPPER_HEX_DIGITS[8 | (cp2 >>>= 4) & 3];
            dest[8] = UPPER_HEX_DIGITS[(cp2 >>>= 2) & 0xF];
            dest[7] = UPPER_HEX_DIGITS[8 | (cp2 >>>= 4) & 3];
            dest[5] = UPPER_HEX_DIGITS[(cp2 >>>= 2) & 0xF];
            dest[4] = UPPER_HEX_DIGITS[8 | (cp2 >>>= 4) & 3];
            dest[2] = UPPER_HEX_DIGITS[(cp2 >>>= 2) & 7];
            return dest;
        }
        throw new IllegalArgumentException("Invalid unicode character value " + cp2);
    }
}

