package org.apache.commons.codec.binary;

import java.nio.charset.*;
import java.nio.*;
import org.apache.commons.codec.*;
import java.io.*;

public class StringUtils
{
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return cs1.length() == cs2.length() && CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, cs1.length());
    }
    
    private static byte[] getBytes(final String string, final Charset charset) {
        if (string == null) {
            return null;
        }
        return string.getBytes(charset);
    }
    
    private static ByteBuffer getByteBuffer(final String string, final Charset charset) {
        if (string == null) {
            return null;
        }
        return ByteBuffer.wrap(string.getBytes(charset));
    }
    
    public static ByteBuffer getByteBufferUtf8(final String string) {
        return getByteBuffer(string, Charsets.UTF_8);
    }
    
    public static byte[] getBytesIso8859_1(final String string) {
        return getBytes(string, Charsets.ISO_8859_1);
    }
    
    public static byte[] getBytesUnchecked(final String string, final String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return string.getBytes(charsetName);
        }
        catch (UnsupportedEncodingException e) {
            throw newIllegalStateException(charsetName, e);
        }
    }
    
    public static byte[] getBytesUsAscii(final String string) {
        return getBytes(string, Charsets.US_ASCII);
    }
    
    public static byte[] getBytesUtf16(final String string) {
        return getBytes(string, Charsets.UTF_16);
    }
    
    public static byte[] getBytesUtf16Be(final String string) {
        return getBytes(string, Charsets.UTF_16BE);
    }
    
    public static byte[] getBytesUtf16Le(final String string) {
        return getBytes(string, Charsets.UTF_16LE);
    }
    
    public static byte[] getBytesUtf8(final String string) {
        return getBytes(string, Charsets.UTF_8);
    }
    
    private static IllegalStateException newIllegalStateException(final String charsetName, final UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }
    
    private static String newString(final byte[] bytes, final Charset charset) {
        return (bytes == null) ? null : new String(bytes, charset);
    }
    
    public static String newString(final byte[] bytes, final String charsetName) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charsetName);
        }
        catch (UnsupportedEncodingException e) {
            throw newIllegalStateException(charsetName, e);
        }
    }
    
    public static String newStringIso8859_1(final byte[] bytes) {
        return newString(bytes, Charsets.ISO_8859_1);
    }
    
    public static String newStringUsAscii(final byte[] bytes) {
        return newString(bytes, Charsets.US_ASCII);
    }
    
    public static String newStringUtf16(final byte[] bytes) {
        return newString(bytes, Charsets.UTF_16);
    }
    
    public static String newStringUtf16Be(final byte[] bytes) {
        return newString(bytes, Charsets.UTF_16BE);
    }
    
    public static String newStringUtf16Le(final byte[] bytes) {
        return newString(bytes, Charsets.UTF_16LE);
    }
    
    public static String newStringUtf8(final byte[] bytes) {
        return newString(bytes, Charsets.UTF_8);
    }
}
