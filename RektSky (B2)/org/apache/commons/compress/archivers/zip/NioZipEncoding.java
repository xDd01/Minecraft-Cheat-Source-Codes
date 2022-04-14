package org.apache.commons.compress.archivers.zip;

import java.nio.*;
import java.io.*;
import java.nio.charset.*;

class NioZipEncoding implements ZipEncoding, CharsetAccessor
{
    private final Charset charset;
    private final boolean useReplacement;
    private static final char REPLACEMENT = '?';
    private static final byte[] REPLACEMENT_BYTES;
    private static final String REPLACEMENT_STRING;
    private static final char[] HEX_CHARS;
    
    NioZipEncoding(final Charset charset, final boolean useReplacement) {
        this.charset = charset;
        this.useReplacement = useReplacement;
    }
    
    @Override
    public Charset getCharset() {
        return this.charset;
    }
    
    @Override
    public boolean canEncode(final String name) {
        final CharsetEncoder enc = this.newEncoder();
        return enc.canEncode(name);
    }
    
    @Override
    public ByteBuffer encode(final String name) {
        final CharsetEncoder enc = this.newEncoder();
        final CharBuffer cb = CharBuffer.wrap(name);
        CharBuffer tmp = null;
        ByteBuffer out = ByteBuffer.allocate(estimateInitialBufferSize(enc, cb.remaining()));
        while (cb.remaining() > 0) {
            final CoderResult res = enc.encode(cb, out, false);
            if (res.isUnmappable() || res.isMalformed()) {
                final int spaceForSurrogate = estimateIncrementalEncodingSize(enc, 6 * res.length());
                if (spaceForSurrogate > out.remaining()) {
                    int charCount = 0;
                    for (int i = cb.position(); i < cb.limit(); ++i) {
                        charCount += (enc.canEncode(cb.get(i)) ? 1 : 6);
                    }
                    final int totalExtraSpace = estimateIncrementalEncodingSize(enc, charCount);
                    out = ZipEncodingHelper.growBufferBy(out, totalExtraSpace - out.remaining());
                }
                if (tmp == null) {
                    tmp = CharBuffer.allocate(6);
                }
                for (int j = 0; j < res.length(); ++j) {
                    out = encodeFully(enc, encodeSurrogate(tmp, cb.get()), out);
                }
            }
            else {
                if (!res.isOverflow()) {
                    continue;
                }
                final int increment = estimateIncrementalEncodingSize(enc, cb.remaining());
                out = ZipEncodingHelper.growBufferBy(out, increment);
            }
        }
        enc.encode(cb, out, true);
        out.limit(out.position());
        out.rewind();
        return out;
    }
    
    @Override
    public String decode(final byte[] data) throws IOException {
        return this.newDecoder().decode(ByteBuffer.wrap(data)).toString();
    }
    
    private static ByteBuffer encodeFully(final CharsetEncoder enc, final CharBuffer cb, final ByteBuffer out) {
        ByteBuffer o = out;
        while (cb.hasRemaining()) {
            final CoderResult result = enc.encode(cb, o, false);
            if (result.isOverflow()) {
                final int increment = estimateIncrementalEncodingSize(enc, cb.remaining());
                o = ZipEncodingHelper.growBufferBy(o, increment);
            }
        }
        return o;
    }
    
    private static CharBuffer encodeSurrogate(final CharBuffer cb, final char c) {
        cb.position(0).limit(6);
        cb.put('%');
        cb.put('U');
        cb.put(NioZipEncoding.HEX_CHARS[c >> 12 & 0xF]);
        cb.put(NioZipEncoding.HEX_CHARS[c >> 8 & 0xF]);
        cb.put(NioZipEncoding.HEX_CHARS[c >> 4 & 0xF]);
        cb.put(NioZipEncoding.HEX_CHARS[c & '\u000f']);
        cb.flip();
        return cb;
    }
    
    private CharsetEncoder newEncoder() {
        if (this.useReplacement) {
            return this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(NioZipEncoding.REPLACEMENT_BYTES);
        }
        return this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    
    private CharsetDecoder newDecoder() {
        if (!this.useReplacement) {
            return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        }
        return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(NioZipEncoding.REPLACEMENT_STRING);
    }
    
    private static int estimateInitialBufferSize(final CharsetEncoder enc, final int charChount) {
        final float first = enc.maxBytesPerChar();
        final float rest = (charChount - 1) * enc.averageBytesPerChar();
        return (int)Math.ceil(first + rest);
    }
    
    private static int estimateIncrementalEncodingSize(final CharsetEncoder enc, final int charCount) {
        return (int)Math.ceil(charCount * enc.averageBytesPerChar());
    }
    
    static {
        REPLACEMENT_BYTES = new byte[] { 63 };
        REPLACEMENT_STRING = String.valueOf('?');
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
