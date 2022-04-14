/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.binary;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

public class BinaryCodec
implements BinaryDecoder,
BinaryEncoder {
    private static final char[] EMPTY_CHAR_ARRAY = new char[0];
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int BIT_0 = 1;
    private static final int BIT_1 = 2;
    private static final int BIT_2 = 4;
    private static final int BIT_3 = 8;
    private static final int BIT_4 = 16;
    private static final int BIT_5 = 32;
    private static final int BIT_6 = 64;
    private static final int BIT_7 = 128;
    private static final int[] BITS = new int[]{1, 2, 4, 8, 16, 32, 64, 128};

    @Override
    public byte[] encode(byte[] raw) {
        return BinaryCodec.toAsciiBytes(raw);
    }

    @Override
    public Object encode(Object raw) throws EncoderException {
        if (!(raw instanceof byte[])) {
            throw new EncoderException("argument not a byte array");
        }
        return BinaryCodec.toAsciiChars((byte[])raw);
    }

    @Override
    public Object decode(Object ascii) throws DecoderException {
        if (ascii == null) {
            return EMPTY_BYTE_ARRAY;
        }
        if (ascii instanceof byte[]) {
            return BinaryCodec.fromAscii((byte[])ascii);
        }
        if (ascii instanceof char[]) {
            return BinaryCodec.fromAscii((char[])ascii);
        }
        if (ascii instanceof String) {
            return BinaryCodec.fromAscii(((String)ascii).toCharArray());
        }
        throw new DecoderException("argument not a byte array");
    }

    @Override
    public byte[] decode(byte[] ascii) {
        return BinaryCodec.fromAscii(ascii);
    }

    public byte[] toByteArray(String ascii) {
        if (ascii == null) {
            return EMPTY_BYTE_ARRAY;
        }
        return BinaryCodec.fromAscii(ascii.toCharArray());
    }

    public static byte[] fromAscii(char[] ascii) {
        if (ascii == null || ascii.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] l_raw = new byte[ascii.length >> 3];
        int ii2 = 0;
        int jj2 = ascii.length - 1;
        while (ii2 < l_raw.length) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                if (ascii[jj2 - bits] != '1') continue;
                int n2 = ii2;
                l_raw[n2] = (byte)(l_raw[n2] | BITS[bits]);
            }
            ++ii2;
            jj2 -= 8;
        }
        return l_raw;
    }

    public static byte[] fromAscii(byte[] ascii) {
        if (BinaryCodec.isEmpty(ascii)) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] l_raw = new byte[ascii.length >> 3];
        int ii2 = 0;
        int jj2 = ascii.length - 1;
        while (ii2 < l_raw.length) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                if (ascii[jj2 - bits] != 49) continue;
                int n2 = ii2;
                l_raw[n2] = (byte)(l_raw[n2] | BITS[bits]);
            }
            ++ii2;
            jj2 -= 8;
        }
        return l_raw;
    }

    private static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static byte[] toAsciiBytes(byte[] raw) {
        if (BinaryCodec.isEmpty(raw)) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] l_ascii = new byte[raw.length << 3];
        int ii2 = 0;
        int jj2 = l_ascii.length - 1;
        while (ii2 < raw.length) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                l_ascii[jj2 - bits] = (raw[ii2] & BITS[bits]) == 0 ? 48 : 49;
            }
            ++ii2;
            jj2 -= 8;
        }
        return l_ascii;
    }

    public static char[] toAsciiChars(byte[] raw) {
        if (BinaryCodec.isEmpty(raw)) {
            return EMPTY_CHAR_ARRAY;
        }
        char[] l_ascii = new char[raw.length << 3];
        int ii2 = 0;
        int jj2 = l_ascii.length - 1;
        while (ii2 < raw.length) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                l_ascii[jj2 - bits] = (raw[ii2] & BITS[bits]) == 0 ? 48 : 49;
            }
            ++ii2;
            jj2 -= 8;
        }
        return l_ascii;
    }

    public static String toAsciiString(byte[] raw) {
        return new String(BinaryCodec.toAsciiChars(raw));
    }
}

