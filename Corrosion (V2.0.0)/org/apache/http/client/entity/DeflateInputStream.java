/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateInputStream
extends InputStream {
    private InputStream sourceStream;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DeflateInputStream(InputStream wrapped) throws IOException {
        byte[] peeked = new byte[6];
        PushbackInputStream pushback = new PushbackInputStream(wrapped, peeked.length);
        int headerLength = pushback.read(peeked);
        if (headerLength == -1) {
            throw new IOException("Unable to read the response");
        }
        byte[] dummy = new byte[1];
        Inflater inf = new Inflater();
        try {
            int n2;
            while ((n2 = inf.inflate(dummy)) == 0) {
                if (inf.finished()) {
                    throw new IOException("Unable to read the response");
                }
                if (inf.needsDictionary()) break;
                if (!inf.needsInput()) continue;
                inf.setInput(peeked);
            }
            if (n2 == -1) {
                throw new IOException("Unable to read the response");
            }
            pushback.unread(peeked, 0, headerLength);
            this.sourceStream = new DeflateStream(pushback, new Inflater());
        }
        catch (DataFormatException e2) {
            pushback.unread(peeked, 0, headerLength);
            this.sourceStream = new DeflateStream(pushback, new Inflater(true));
        }
        finally {
            inf.end();
        }
    }

    public int read() throws IOException {
        return this.sourceStream.read();
    }

    public int read(byte[] b2) throws IOException {
        return this.sourceStream.read(b2);
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        return this.sourceStream.read(b2, off, len);
    }

    public long skip(long n2) throws IOException {
        return this.sourceStream.skip(n2);
    }

    public int available() throws IOException {
        return this.sourceStream.available();
    }

    public void mark(int readLimit) {
        this.sourceStream.mark(readLimit);
    }

    public void reset() throws IOException {
        this.sourceStream.reset();
    }

    public boolean markSupported() {
        return this.sourceStream.markSupported();
    }

    public void close() throws IOException {
        this.sourceStream.close();
    }

    static class DeflateStream
    extends InflaterInputStream {
        private boolean closed = false;

        public DeflateStream(InputStream in2, Inflater inflater) {
            super(in2, inflater);
        }

        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.inf.end();
            super.close();
        }
    }
}

