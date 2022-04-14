/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodecInputStream;

public class Base64InputStream
extends BaseNCodecInputStream {
    public Base64InputStream(InputStream in2) {
        this(in2, false);
    }

    public Base64InputStream(InputStream in2, boolean doEncode) {
        super(in2, new Base64(false), doEncode);
    }

    public Base64InputStream(InputStream in2, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(in2, new Base64(lineLength, lineSeparator), doEncode);
    }
}

