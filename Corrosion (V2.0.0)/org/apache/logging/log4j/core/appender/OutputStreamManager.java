/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;

public class OutputStreamManager
extends AbstractManager {
    private volatile OutputStream os;
    private final byte[] footer;
    private final byte[] header;

    protected OutputStreamManager(OutputStream os2, String streamName, Layout<?> layout) {
        super(streamName);
        this.os = os2;
        if (layout != null) {
            this.footer = layout.getFooter();
            this.header = layout.getHeader();
            if (this.header != null) {
                try {
                    this.os.write(this.header, 0, this.header.length);
                }
                catch (IOException ioe) {
                    LOGGER.error("Unable to write header", (Throwable)ioe);
                }
            }
        } else {
            this.footer = null;
            this.header = null;
        }
    }

    public static <T> OutputStreamManager getManager(String name, T data, ManagerFactory<? extends OutputStreamManager, T> factory) {
        return AbstractManager.getManager(name, factory, data);
    }

    @Override
    public void releaseSub() {
        if (this.footer != null) {
            this.write(this.footer);
        }
        this.close();
    }

    public boolean isOpen() {
        return this.getCount() > 0;
    }

    protected OutputStream getOutputStream() {
        return this.os;
    }

    protected void setOutputStream(OutputStream os2) {
        if (this.header != null) {
            try {
                os2.write(this.header, 0, this.header.length);
                this.os = os2;
            }
            catch (IOException ioe) {
                LOGGER.error("Unable to write header", (Throwable)ioe);
            }
        } else {
            this.os = os2;
        }
    }

    protected synchronized void write(byte[] bytes, int offset, int length) {
        try {
            this.os.write(bytes, offset, length);
        }
        catch (IOException ex2) {
            String msg = "Error writing to stream " + this.getName();
            throw new AppenderLoggingException(msg, ex2);
        }
    }

    protected void write(byte[] bytes) {
        this.write(bytes, 0, bytes.length);
    }

    protected synchronized void close() {
        OutputStream stream = this.os;
        if (stream == System.out || stream == System.err) {
            return;
        }
        try {
            stream.close();
        }
        catch (IOException ex2) {
            LOGGER.error("Unable to close stream " + this.getName() + ". " + ex2);
        }
    }

    public synchronized void flush() {
        try {
            this.os.flush();
        }
        catch (IOException ex2) {
            String msg = "Error flushing stream " + this.getName();
            throw new AppenderLoggingException(msg, ex2);
        }
    }
}

