/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

class Simple8BitZipEncoding
implements ZipEncoding {
    private final char[] highChars;
    private final List<Simple8BitChar> reverseMapping;

    public Simple8BitZipEncoding(char[] highChars) {
        this.highChars = (char[])highChars.clone();
        ArrayList<Simple8BitChar> temp = new ArrayList<Simple8BitChar>(this.highChars.length);
        byte code = 127;
        for (char highChar : this.highChars) {
            code = (byte)(code + 1);
            temp.add(new Simple8BitChar(code, highChar));
        }
        Collections.sort(temp);
        this.reverseMapping = Collections.unmodifiableList(temp);
    }

    public char decodeByte(byte b2) {
        if (b2 >= 0) {
            return (char)b2;
        }
        return this.highChars[128 + b2];
    }

    public boolean canEncodeChar(char c2) {
        if (c2 >= '\u0000' && c2 < '\u0080') {
            return true;
        }
        Simple8BitChar r2 = this.encodeHighChar(c2);
        return r2 != null;
    }

    public boolean pushEncodedChar(ByteBuffer bb2, char c2) {
        if (c2 >= '\u0000' && c2 < '\u0080') {
            bb2.put((byte)c2);
            return true;
        }
        Simple8BitChar r2 = this.encodeHighChar(c2);
        if (r2 == null) {
            return false;
        }
        bb2.put(r2.code);
        return true;
    }

    private Simple8BitChar encodeHighChar(char c2) {
        int i0 = 0;
        int i1 = this.reverseMapping.size();
        while (i1 > i0) {
            int i2 = i0 + (i1 - i0) / 2;
            Simple8BitChar m2 = this.reverseMapping.get(i2);
            if (m2.unicode == c2) {
                return m2;
            }
            if (m2.unicode < c2) {
                i0 = i2 + 1;
                continue;
            }
            i1 = i2;
        }
        if (i0 >= this.reverseMapping.size()) {
            return null;
        }
        Simple8BitChar r2 = this.reverseMapping.get(i0);
        if (r2.unicode != c2) {
            return null;
        }
        return r2;
    }

    public boolean canEncode(String name) {
        for (int i2 = 0; i2 < name.length(); ++i2) {
            char c2 = name.charAt(i2);
            if (this.canEncodeChar(c2)) continue;
            return false;
        }
        return true;
    }

    public ByteBuffer encode(String name) {
        ByteBuffer out = ByteBuffer.allocate(name.length() + 6 + (name.length() + 1) / 2);
        for (int i2 = 0; i2 < name.length(); ++i2) {
            char c2 = name.charAt(i2);
            if (out.remaining() < 6) {
                out = ZipEncodingHelper.growBuffer(out, out.position() + 6);
            }
            if (this.pushEncodedChar(out, c2)) continue;
            ZipEncodingHelper.appendSurrogate(out, c2);
        }
        out.limit(out.position());
        out.rewind();
        return out;
    }

    public String decode(byte[] data) throws IOException {
        char[] ret = new char[data.length];
        for (int i2 = 0; i2 < data.length; ++i2) {
            ret[i2] = this.decodeByte(data[i2]);
        }
        return new String(ret);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Simple8BitChar
    implements Comparable<Simple8BitChar> {
        public final char unicode;
        public final byte code;

        Simple8BitChar(byte code, char unicode) {
            this.code = code;
            this.unicode = unicode;
        }

        @Override
        public int compareTo(Simple8BitChar a2) {
            return this.unicode - a2.unicode;
        }

        public String toString() {
            return "0x" + Integer.toHexString(0xFFFF & this.unicode) + "->0x" + Integer.toHexString(0xFF & this.code);
        }

        public boolean equals(Object o2) {
            if (o2 instanceof Simple8BitChar) {
                Simple8BitChar other = (Simple8BitChar)o2;
                return this.unicode == other.unicode && this.code == other.code;
            }
            return false;
        }

        public int hashCode() {
            return this.unicode;
        }
    }
}

