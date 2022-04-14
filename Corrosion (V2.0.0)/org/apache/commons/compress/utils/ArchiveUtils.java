/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.utils;

import java.io.UnsupportedEncodingException;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class ArchiveUtils {
    private ArchiveUtils() {
    }

    public static String toString(ArchiveEntry entry) {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(entry.isDirectory() ? (char)'d' : '-');
        String size = Long.toString(entry.getSize());
        sb2.append(' ');
        for (int i2 = 7; i2 > size.length(); --i2) {
            sb2.append(' ');
        }
        sb2.append(size);
        sb2.append(' ').append(entry.getName());
        return sb2.toString();
    }

    public static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length) {
        byte[] buffer1;
        try {
            buffer1 = expected.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
        return ArchiveUtils.isEqual(buffer1, 0, buffer1.length, buffer, offset, length, false);
    }

    public static boolean matchAsciiBuffer(String expected, byte[] buffer) {
        return ArchiveUtils.matchAsciiBuffer(expected, buffer, 0, buffer.length);
    }

    public static byte[] toAsciiBytes(String inputString) {
        try {
            return inputString.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String toAsciiString(byte[] inputBytes) {
        try {
            return new String(inputBytes, "US-ASCII");
        }
        catch (UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String toAsciiString(byte[] inputBytes, int offset, int length) {
        try {
            return new String(inputBytes, offset, length, "US-ASCII");
        }
        catch (UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2, boolean ignoreTrailingNulls) {
        int i2;
        int minLen = length1 < length2 ? length1 : length2;
        for (i2 = 0; i2 < minLen; ++i2) {
            if (buffer1[offset1 + i2] == buffer2[offset2 + i2]) continue;
            return false;
        }
        if (length1 == length2) {
            return true;
        }
        if (ignoreTrailingNulls) {
            if (length1 > length2) {
                for (i2 = length2; i2 < length1; ++i2) {
                    if (buffer1[offset1 + i2] == 0) continue;
                    return false;
                }
            } else {
                for (i2 = length1; i2 < length2; ++i2) {
                    if (buffer2[offset2 + i2] == 0) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
        return ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, false);
    }

    public static boolean isEqual(byte[] buffer1, byte[] buffer2) {
        return ArchiveUtils.isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, false);
    }

    public static boolean isEqual(byte[] buffer1, byte[] buffer2, boolean ignoreTrailingNulls) {
        return ArchiveUtils.isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, ignoreTrailingNulls);
    }

    public static boolean isEqualWithNull(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
        return ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, true);
    }

    public static boolean isArrayZero(byte[] a2, int size) {
        for (int i2 = 0; i2 < size; ++i2) {
            if (a2[i2] == 0) continue;
            return false;
        }
        return true;
    }
}

