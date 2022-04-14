/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.http.Consts;
import org.apache.http.annotation.Immutable;
import org.apache.http.impl.conn.Wire;
import org.apache.http.io.EofSensor;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
@Immutable
public class LoggingSessionInputBuffer
implements SessionInputBuffer,
EofSensor {
    private final SessionInputBuffer in;
    private final EofSensor eofSensor;
    private final Wire wire;
    private final String charset;

    public LoggingSessionInputBuffer(SessionInputBuffer in2, Wire wire, String charset) {
        this.in = in2;
        this.eofSensor = in2 instanceof EofSensor ? (EofSensor)((Object)in2) : null;
        this.wire = wire;
        this.charset = charset != null ? charset : Consts.ASCII.name();
    }

    public LoggingSessionInputBuffer(SessionInputBuffer in2, Wire wire) {
        this(in2, wire, null);
    }

    public boolean isDataAvailable(int timeout) throws IOException {
        return this.in.isDataAvailable(timeout);
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        int l2 = this.in.read(b2, off, len);
        if (this.wire.enabled() && l2 > 0) {
            this.wire.input(b2, off, l2);
        }
        return l2;
    }

    public int read() throws IOException {
        int l2 = this.in.read();
        if (this.wire.enabled() && l2 != -1) {
            this.wire.input(l2);
        }
        return l2;
    }

    public int read(byte[] b2) throws IOException {
        int l2 = this.in.read(b2);
        if (this.wire.enabled() && l2 > 0) {
            this.wire.input(b2, 0, l2);
        }
        return l2;
    }

    public String readLine() throws IOException {
        String s2 = this.in.readLine();
        if (this.wire.enabled() && s2 != null) {
            String tmp = s2 + "\r\n";
            this.wire.input(tmp.getBytes(this.charset));
        }
        return s2;
    }

    public int readLine(CharArrayBuffer buffer) throws IOException {
        int l2 = this.in.readLine(buffer);
        if (this.wire.enabled() && l2 >= 0) {
            int pos = buffer.length() - l2;
            String s2 = new String(buffer.buffer(), pos, l2);
            String tmp = s2 + "\r\n";
            this.wire.input(tmp.getBytes(this.charset));
        }
        return l2;
    }

    public HttpTransportMetrics getMetrics() {
        return this.in.getMetrics();
    }

    public boolean isEof() {
        if (this.eofSensor != null) {
            return this.eofSensor.isEof();
        }
        return false;
    }
}

