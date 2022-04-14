/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.Args;

@Immutable
public class Wire {
    private final Log log;
    private final String id;

    public Wire(Log log, String id2) {
        this.log = log;
        this.id = id2;
    }

    public Wire(Log log) {
        this(log, "");
    }

    private void wire(String header, InputStream instream) throws IOException {
        int ch;
        StringBuilder buffer = new StringBuilder();
        while ((ch = instream.read()) != -1) {
            if (ch == 13) {
                buffer.append("[\\r]");
                continue;
            }
            if (ch == 10) {
                buffer.append("[\\n]\"");
                buffer.insert(0, "\"");
                buffer.insert(0, header);
                this.log.debug(this.id + " " + buffer.toString());
                buffer.setLength(0);
                continue;
            }
            if (ch < 32 || ch > 127) {
                buffer.append("[0x");
                buffer.append(Integer.toHexString(ch));
                buffer.append("]");
                continue;
            }
            buffer.append((char)ch);
        }
        if (buffer.length() > 0) {
            buffer.append('\"');
            buffer.insert(0, '\"');
            buffer.insert(0, header);
            this.log.debug(this.id + " " + buffer.toString());
        }
    }

    public boolean enabled() {
        return this.log.isDebugEnabled();
    }

    public void output(InputStream outstream) throws IOException {
        Args.notNull(outstream, "Output");
        this.wire(">> ", outstream);
    }

    public void input(InputStream instream) throws IOException {
        Args.notNull(instream, "Input");
        this.wire("<< ", instream);
    }

    public void output(byte[] b2, int off, int len) throws IOException {
        Args.notNull(b2, "Output");
        this.wire(">> ", new ByteArrayInputStream(b2, off, len));
    }

    public void input(byte[] b2, int off, int len) throws IOException {
        Args.notNull(b2, "Input");
        this.wire("<< ", new ByteArrayInputStream(b2, off, len));
    }

    public void output(byte[] b2) throws IOException {
        Args.notNull(b2, "Output");
        this.wire(">> ", new ByteArrayInputStream(b2));
    }

    public void input(byte[] b2) throws IOException {
        Args.notNull(b2, "Input");
        this.wire("<< ", new ByteArrayInputStream(b2));
    }

    public void output(int b2) throws IOException {
        this.output(new byte[]{(byte)b2});
    }

    public void input(int b2) throws IOException {
        this.input(new byte[]{(byte)b2});
    }

    public void output(String s2) throws IOException {
        Args.notNull(s2, "Output");
        this.output(s2.getBytes());
    }

    public void input(String s2) throws IOException {
        Args.notNull(s2, "Input");
        this.input(s2.getBytes());
    }
}

