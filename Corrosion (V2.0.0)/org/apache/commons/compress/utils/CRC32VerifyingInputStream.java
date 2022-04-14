/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.utils;

import java.io.InputStream;
import java.util.zip.CRC32;
import org.apache.commons.compress.utils.ChecksumVerifyingInputStream;

public class CRC32VerifyingInputStream
extends ChecksumVerifyingInputStream {
    public CRC32VerifyingInputStream(InputStream in2, long size, int expectedCrc32) {
        this(in2, size, (long)expectedCrc32 & 0xFFFFFFFFL);
    }

    public CRC32VerifyingInputStream(InputStream in2, long size, long expectedCrc32) {
        super(new CRC32(), in2, size, expectedCrc32);
    }
}

