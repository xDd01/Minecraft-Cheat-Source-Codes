/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {
    private static final int COPY_BUF_SIZE = 8024;
    private static final int SKIP_BUF_SIZE = 4096;
    private static final byte[] SKIP_BUF = new byte[4096];

    private IOUtils() {
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        return IOUtils.copy(input, output, 8024);
    }

    public static long copy(InputStream input, OutputStream output, int buffersize) throws IOException {
        byte[] buffer = new byte[buffersize];
        int n2 = 0;
        long count = 0L;
        while (-1 != (n2 = input.read(buffer))) {
            output.write(buffer, 0, n2);
            count += (long)n2;
        }
        return count;
    }

    public static long skip(InputStream input, long numToSkip) throws IOException {
        int read;
        long skipped;
        long available = numToSkip;
        while (numToSkip > 0L && (skipped = input.skip(numToSkip)) != 0L) {
            numToSkip -= skipped;
        }
        while (numToSkip > 0L && (read = IOUtils.readFully(input, SKIP_BUF, 0, (int)Math.min(numToSkip, 4096L))) >= 1) {
            numToSkip -= (long)read;
        }
        return available - numToSkip;
    }

    public static int readFully(InputStream input, byte[] b2) throws IOException {
        return IOUtils.readFully(input, b2, 0, b2.length);
    }

    public static int readFully(InputStream input, byte[] b2, int offset, int len) throws IOException {
        int count;
        if (len < 0 || offset < 0 || len + offset > b2.length) {
            throw new IndexOutOfBoundsException();
        }
        int x2 = 0;
        for (count = 0; count != len && (x2 = input.read(b2, offset + count, len - count)) != -1; count += x2) {
        }
        return count;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(input, output);
        return output.toByteArray();
    }

    public static void closeQuietly(Closeable c2) {
        if (c2 != null) {
            try {
                c2.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

