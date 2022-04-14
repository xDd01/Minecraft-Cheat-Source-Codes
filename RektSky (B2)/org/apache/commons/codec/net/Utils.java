package org.apache.commons.codec.net;

import org.apache.commons.codec.*;

class Utils
{
    private static final int RADIX = 16;
    
    static int digit16(final byte b) throws DecoderException {
        final int i = Character.digit((char)b, 16);
        if (i == -1) {
            throw new DecoderException("Invalid URL encoding: not a valid digit (radix 16): " + b);
        }
        return i;
    }
    
    static char hexDigit(final int b) {
        return Character.toUpperCase(Character.forDigit(b & 0xF, 16));
    }
}
