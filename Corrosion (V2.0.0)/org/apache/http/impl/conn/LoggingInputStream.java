/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.conn.Wire;

@NotThreadSafe
class LoggingInputStream
extends InputStream {
    private final InputStream in;
    private final Wire wire;

    public LoggingInputStream(InputStream in2, Wire wire) {
        this.in = in2;
        this.wire = wire;
    }

    public int read() throws IOException {
        try {
            int b2 = this.in.read();
            if (b2 == -1) {
                this.wire.input("end of stream");
            } else {
                this.wire.input(b2);
            }
            return b2;
        }
        catch (IOException ex2) {
            this.wire.input("[read] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public int read(byte[] b2) throws IOException {
        try {
            int bytesRead = this.in.read(b2);
            if (bytesRead == -1) {
                this.wire.input("end of stream");
            } else if (bytesRead > 0) {
                this.wire.input(b2, 0, bytesRead);
            }
            return bytesRead;
        }
        catch (IOException ex2) {
            this.wire.input("[read] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        try {
            int bytesRead = this.in.read(b2, off, len);
            if (bytesRead == -1) {
                this.wire.input("end of stream");
            } else if (bytesRead > 0) {
                this.wire.input(b2, off, bytesRead);
            }
            return bytesRead;
        }
        catch (IOException ex2) {
            this.wire.input("[read] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public long skip(long n2) throws IOException {
        try {
            return super.skip(n2);
        }
        catch (IOException ex2) {
            this.wire.input("[skip] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public int available() throws IOException {
        try {
            return this.in.available();
        }
        catch (IOException ex2) {
            this.wire.input("[available] I/O error : " + ex2.getMessage());
            throw ex2;
        }
    }

    public void mark(int readlimit) {
        super.mark(readlimit);
    }

    public void reset() throws IOException {
        super.reset();
    }

    public boolean markSupported() {
        return false;
    }

    public void close() throws IOException {
        try {
            this.in.close();
        }
        catch (IOException ex2) {
            this.wire.input("[close] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }
}

