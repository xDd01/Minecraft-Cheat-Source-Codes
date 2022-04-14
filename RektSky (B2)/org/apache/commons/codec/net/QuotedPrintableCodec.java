package org.apache.commons.codec.net;

import java.util.*;
import java.nio.charset.*;
import org.apache.commons.codec.*;
import org.apache.commons.codec.binary.*;
import java.io.*;

public class QuotedPrintableCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
{
    private final Charset charset;
    private final boolean strict;
    private static final BitSet PRINTABLE_CHARS;
    private static final byte ESCAPE_CHAR = 61;
    private static final byte TAB = 9;
    private static final byte SPACE = 32;
    private static final byte CR = 13;
    private static final byte LF = 10;
    private static final int SAFE_LENGTH = 73;
    
    public QuotedPrintableCodec() {
        this(Charsets.UTF_8, false);
    }
    
    public QuotedPrintableCodec(final boolean strict) {
        this(Charsets.UTF_8, strict);
    }
    
    public QuotedPrintableCodec(final Charset charset) {
        this(charset, false);
    }
    
    public QuotedPrintableCodec(final Charset charset, final boolean strict) {
        this.charset = charset;
        this.strict = strict;
    }
    
    public QuotedPrintableCodec(final String charsetName) throws IllegalCharsetNameException, IllegalArgumentException, UnsupportedCharsetException {
        this(Charset.forName(charsetName), false);
    }
    
    private static final int encodeQuotedPrintable(final int b, final ByteArrayOutputStream buffer) {
        buffer.write(61);
        final char hex1 = Utils.hexDigit(b >> 4);
        final char hex2 = Utils.hexDigit(b);
        buffer.write(hex1);
        buffer.write(hex2);
        return 3;
    }
    
    private static int getUnsignedOctet(final int index, final byte[] bytes) {
        int b = bytes[index];
        if (b < 0) {
            b += 256;
        }
        return b;
    }
    
    private static int encodeByte(final int b, final boolean encode, final ByteArrayOutputStream buffer) {
        if (encode) {
            return encodeQuotedPrintable(b, buffer);
        }
        buffer.write(b);
        return 1;
    }
    
    private static boolean isWhitespace(final int b) {
        return b == 32 || b == 9;
    }
    
    public static final byte[] encodeQuotedPrintable(final BitSet printable, final byte[] bytes) {
        return encodeQuotedPrintable(printable, bytes, false);
    }
    
    public static final byte[] encodeQuotedPrintable(BitSet printable, final byte[] bytes, final boolean strict) {
        if (bytes == null) {
            return null;
        }
        if (printable == null) {
            printable = QuotedPrintableCodec.PRINTABLE_CHARS;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        if (strict) {
            int pos = 1;
            for (int i = 0; i < bytes.length - 3; ++i) {
                final int b = getUnsignedOctet(i, bytes);
                if (pos < 73) {
                    pos += encodeByte(b, !printable.get(b), buffer);
                }
                else {
                    encodeByte(b, !printable.get(b) || isWhitespace(b), buffer);
                    buffer.write(61);
                    buffer.write(13);
                    buffer.write(10);
                    pos = 1;
                }
            }
            int b2 = getUnsignedOctet(bytes.length - 3, bytes);
            boolean encode = !printable.get(b2) || (isWhitespace(b2) && pos > 68);
            pos += encodeByte(b2, encode, buffer);
            if (pos > 71) {
                buffer.write(61);
                buffer.write(13);
                buffer.write(10);
            }
            for (int j = bytes.length - 2; j < bytes.length; ++j) {
                b2 = getUnsignedOctet(j, bytes);
                encode = (!printable.get(b2) || (j > bytes.length - 2 && isWhitespace(b2)));
                encodeByte(b2, encode, buffer);
            }
        }
        else {
            for (int b3 : bytes) {
                final byte c = (byte)b3;
                if (b3 < 0) {
                    b3 += 256;
                }
                if (printable.get(b3)) {
                    buffer.write(b3);
                }
                else {
                    encodeQuotedPrintable(b3, buffer);
                }
            }
        }
        return buffer.toByteArray();
    }
    
    public static final byte[] decodeQuotedPrintable(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; ++i) {
            final int b = bytes[i];
            if (b == 61) {
                try {
                    if (bytes[++i] == 13) {
                        continue;
                    }
                    final int u = Utils.digit16(bytes[i]);
                    final int l = Utils.digit16(bytes[++i]);
                    buffer.write((char)((u << 4) + l));
                    continue;
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid quoted-printable encoding", e);
                }
            }
            if (b != 13 && b != 10) {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }
    
    @Override
    public byte[] encode(final byte[] bytes) {
        return encodeQuotedPrintable(QuotedPrintableCodec.PRINTABLE_CHARS, bytes, this.strict);
    }
    
    @Override
    public byte[] decode(final byte[] bytes) throws DecoderException {
        return decodeQuotedPrintable(bytes);
    }
    
    @Override
    public String encode(final String str) throws EncoderException {
        return this.encode(str, this.getCharset());
    }
    
    public String decode(final String str, final Charset charset) throws DecoderException {
        if (str == null) {
            return null;
        }
        return new String(this.decode(StringUtils.getBytesUsAscii(str)), charset);
    }
    
    public String decode(final String str, final String charset) throws DecoderException, UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return new String(this.decode(StringUtils.getBytesUsAscii(str)), charset);
    }
    
    @Override
    public String decode(final String str) throws DecoderException {
        return this.decode(str, this.getCharset());
    }
    
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return this.encode((byte[])obj);
        }
        if (obj instanceof String) {
            return this.encode((String)obj);
        }
        throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be quoted-printable encoded");
    }
    
    @Override
    public Object decode(final Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return this.decode((byte[])obj);
        }
        if (obj instanceof String) {
            return this.decode((String)obj);
        }
        throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be quoted-printable decoded");
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public String getDefaultCharset() {
        return this.charset.name();
    }
    
    public String encode(final String str, final Charset charset) {
        if (str == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(this.encode(str.getBytes(charset)));
    }
    
    public String encode(final String str, final String charset) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(this.encode(str.getBytes(charset)));
    }
    
    static {
        PRINTABLE_CHARS = new BitSet(256);
        for (int i = 33; i <= 60; ++i) {
            QuotedPrintableCodec.PRINTABLE_CHARS.set(i);
        }
        for (int i = 62; i <= 126; ++i) {
            QuotedPrintableCodec.PRINTABLE_CHARS.set(i);
        }
        QuotedPrintableCodec.PRINTABLE_CHARS.set(9);
        QuotedPrintableCodec.PRINTABLE_CHARS.set(32);
    }
}
