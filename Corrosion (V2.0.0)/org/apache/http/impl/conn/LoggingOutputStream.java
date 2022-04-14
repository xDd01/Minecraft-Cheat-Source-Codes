/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.conn.Wire;

@NotThreadSafe
class LoggingOutputStream
extends OutputStream {
    private final OutputStream out;
    private final Wire wire;

    public LoggingOutputStream(OutputStream out, Wire wire) {
        this.out = out;
        this.wire = wire;
    }

    public void write(int b2) throws IOException {
        try {
            this.wire.output(b2);
        }
        catch (IOException ex2) {
            this.wire.output("[write] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public void write(byte[] b2) throws IOException {
        try {
            this.wire.output(b2);
            this.out.write(b2);
        }
        catch (IOException ex2) {
            this.wire.output("[write] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public void write(byte[] b2, int off, int len) throws IOException {
        try {
            this.wire.output(b2, off, len);
            this.out.write(b2, off, len);
        }
        catch (IOException ex2) {
            this.wire.output("[write] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public void flush() throws IOException {
        try {
            this.out.flush();
        }
        catch (IOException ex2) {
            this.wire.output("[flush] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }

    public void close() throws IOException {
        try {
            this.out.close();
        }
        catch (IOException ex2) {
            this.wire.output("[close] I/O error: " + ex2.getMessage());
            throw ex2;
        }
    }
}

