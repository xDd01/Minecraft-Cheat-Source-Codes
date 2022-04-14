/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.http.Consts;
import org.apache.http.annotation.Immutable;
import org.apache.http.impl.conn.Wire;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
@Immutable
public class LoggingSessionOutputBuffer
implements SessionOutputBuffer {
    private final SessionOutputBuffer out;
    private final Wire wire;
    private final String charset;

    public LoggingSessionOutputBuffer(SessionOutputBuffer out, Wire wire, String charset) {
        this.out = out;
        this.wire = wire;
        this.charset = charset != null ? charset : Consts.ASCII.name();
    }

    public LoggingSessionOutputBuffer(SessionOutputBuffer out, Wire wire) {
        this(out, wire, null);
    }

    public void write(byte[] b2, int off, int len) throws IOException {
        this.out.write(b2, off, len);
        if (this.wire.enabled()) {
            this.wire.output(b2, off, len);
        }
    }

    public void write(int b2) throws IOException {
        this.out.write(b2);
        if (this.wire.enabled()) {
            this.wire.output(b2);
        }
    }

    public void write(byte[] b2) throws IOException {
        this.out.write(b2);
        if (this.wire.enabled()) {
            this.wire.output(b2);
        }
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void writeLine(CharArrayBuffer buffer) throws IOException {
        this.out.writeLine(buffer);
        if (this.wire.enabled()) {
            String s2 = new String(buffer.buffer(), 0, buffer.length());
            String tmp = s2 + "\r\n";
            this.wire.output(tmp.getBytes(this.charset));
        }
    }

    public void writeLine(String s2) throws IOException {
        this.out.writeLine(s2);
        if (this.wire.enabled()) {
            String tmp = s2 + "\r\n";
            this.wire.output(tmp.getBytes(this.charset));
        }
    }

    public HttpTransportMetrics getMetrics() {
        return this.out.getMetrics();
    }
}

