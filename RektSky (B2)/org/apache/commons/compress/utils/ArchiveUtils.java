package org.apache.commons.compress.utils;

import org.apache.commons.compress.archivers.*;
import java.io.*;
import java.util.*;

public class ArchiveUtils
{
    private static final int MAX_SANITIZED_NAME_LENGTH = 255;
    
    private ArchiveUtils() {
    }
    
    public static String toString(final ArchiveEntry entry) {
        final StringBuilder sb = new StringBuilder();
        sb.append(entry.isDirectory() ? 'd' : '-');
        final String size = Long.toString(entry.getSize());
        sb.append(' ');
        for (int i = 7; i > size.length(); --i) {
            sb.append(' ');
        }
        sb.append(size);
        sb.append(' ').append(entry.getName());
        return sb.toString();
    }
    
    public static boolean matchAsciiBuffer(final String expected, final byte[] buffer, final int offset, final int length) {
        byte[] buffer2;
        try {
            buffer2 = expected.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return isEqual(buffer2, 0, buffer2.length, buffer, offset, length, false);
    }
    
    public static boolean matchAsciiBuffer(final String expected, final byte[] buffer) {
        return matchAsciiBuffer(expected, buffer, 0, buffer.length);
    }
    
    public static byte[] toAsciiBytes(final String inputString) {
        try {
            return inputString.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String toAsciiString(final byte[] inputBytes) {
        try {
            return new String(inputBytes, "US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String toAsciiString(final byte[] inputBytes, final int offset, final int length) {
        try {
            return new String(inputBytes, offset, length, "US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean isEqual(final byte[] buffer1, final int offset1, final int length1, final byte[] buffer2, final int offset2, final int length2, final boolean ignoreTrailingNulls) {
        for (int minLen = (length1 < length2) ? length1 : length2, i = 0; i < minLen; ++i) {
            if (buffer1[offset1 + i] != buffer2[offset2 + i]) {
                return false;
            }
        }
        if (length1 == length2) {
            return true;
        }
        if (ignoreTrailingNulls) {
            if (length1 > length2) {
                for (int i = length2; i < length1; ++i) {
                    if (buffer1[offset1 + i] != 0) {
                        return false;
                    }
                }
            }
            else {
                for (int i = length1; i < length2; ++i) {
                    if (buffer2[offset2 + i] != 0) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean isEqual(final byte[] buffer1, final int offset1, final int length1, final byte[] buffer2, final int offset2, final int length2) {
        return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, false);
    }
    
    public static boolean isEqual(final byte[] buffer1, final byte[] buffer2) {
        return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, false);
    }
    
    public static boolean isEqual(final byte[] buffer1, final byte[] buffer2, final boolean ignoreTrailingNulls) {
        return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, ignoreTrailingNulls);
    }
    
    public static boolean isEqualWithNull(final byte[] buffer1, final int offset1, final int length1, final byte[] buffer2, final int offset2, final int length2) {
        return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, true);
    }
    
    public static boolean isArrayZero(final byte[] a, final int size) {
        for (int i = 0; i < size; ++i) {
            if (a[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public static String sanitize(final String s) {
        final char[] cs = s.toCharArray();
        final char[] chars = (cs.length <= 255) ? cs : Arrays.copyOf(cs, 255);
        if (cs.length > 255) {
            for (int i = 252; i < 255; ++i) {
                chars[i] = '.';
            }
        }
        final StringBuilder sb = new StringBuilder();
        for (final char c : chars) {
            Label_0133: {
                if (!Character.isISOControl(c)) {
                    final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
                    if (block != null && block != Character.UnicodeBlock.SPECIALS) {
                        sb.append(c);
                        break Label_0133;
                    }
                }
                sb.append('?');
            }
        }
        return sb.toString();
    }
}
