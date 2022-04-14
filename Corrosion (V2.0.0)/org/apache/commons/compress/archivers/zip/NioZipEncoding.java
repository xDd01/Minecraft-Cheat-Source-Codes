/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

class NioZipEncoding
implements ZipEncoding {
    private final Charset charset;

    public NioZipEncoding(Charset charset) {
        this.charset = charset;
    }

    public boolean canEncode(String name) {
        CharsetEncoder enc = this.charset.newEncoder();
        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);
        return enc.canEncode(name);
    }

    public ByteBuffer encode(String name) {
        CharsetEncoder enc = this.charset.newEncoder();
        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer cb2 = CharBuffer.wrap(name);
        ByteBuffer out = ByteBuffer.allocate(name.length() + (name.length() + 1) / 2);
        while (cb2.remaining() > 0) {
            CoderResult res = enc.encode(cb2, out, true);
            if (res.isUnmappable() || res.isMalformed()) {
                if (res.length() * 6 > out.remaining()) {
                    out = ZipEncodingHelper.growBuffer(out, out.position() + res.length() * 6);
                }
                for (int i2 = 0; i2 < res.length(); ++i2) {
                    ZipEncodingHelper.appendSurrogate(out, cb2.get());
                }
                continue;
            }
            if (res.isOverflow()) {
                out = ZipEncodingHelper.growBuffer(out, 0);
                continue;
            }
            if (!res.isUnderflow()) continue;
            enc.flush(out);
            break;
        }
        out.limit(out.position());
        out.rewind();
        return out;
    }

    public String decode(byte[] data) throws IOException {
        return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(data)).toString();
    }
}

