/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.BaseNCodecInputStream;

public class Base32InputStream
extends BaseNCodecInputStream {
    public Base32InputStream(InputStream in2) {
        this(in2, false);
    }

    public Base32InputStream(InputStream in2, boolean doEncode) {
        super(in2, new Base32(false), doEncode);
    }

    public Base32InputStream(InputStream in2, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(in2, new Base32(lineLength, lineSeparator), doEncode);
    }
}

