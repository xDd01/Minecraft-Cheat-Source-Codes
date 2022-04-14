/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.net;

import org.apache.commons.codec.DecoderException;

class Utils {
    Utils() {
    }

    static int digit16(byte b2) throws DecoderException {
        int i2 = Character.digit((char)b2, 16);
        if (i2 == -1) {
            throw new DecoderException("Invalid URL encoding: not a valid digit (radix 16): " + b2);
        }
        return i2;
    }
}

