package org.apache.commons.compress.utils;

import java.io.*;
import java.util.zip.*;

public class CRC32VerifyingInputStream extends ChecksumVerifyingInputStream
{
    public CRC32VerifyingInputStream(final InputStream in, final long size, final int expectedCrc32) {
        this(in, size, (long)expectedCrc32 & 0xFFFFFFFFL);
    }
    
    public CRC32VerifyingInputStream(final InputStream in, final long size, final long expectedCrc32) {
        super(new CRC32(), in, size, expectedCrc32);
    }
}
