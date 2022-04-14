package org.apache.http.conn;

import org.apache.http.util.*;
import java.io.*;

public class EofSensorInputStream extends InputStream implements ConnectionReleaseTrigger
{
    protected InputStream wrappedStream;
    private boolean selfClosed;
    private final EofSensorWatcher eofWatcher;
    
    public EofSensorInputStream(final InputStream in, final EofSensorWatcher watcher) {
        Args.notNull(in, "Wrapped stream");
        this.wrappedStream = in;
        this.selfClosed = false;
        this.eofWatcher = watcher;
    }
    
    boolean isSelfClosed() {
        return this.selfClosed;
    }
    
    InputStream getWrappedStream() {
        return this.wrappedStream;
    }
    
    protected boolean isReadAllowed() throws IOException {
        if (this.selfClosed) {
            throw new IOException("Attempted read on closed stream.");
        }
        return this.wrappedStream != null;
    }
    
    @Override
    public int read() throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            try {
                l = this.wrappedStream.read();
                this.checkEOF(l);
            }
            catch (IOException ex) {
                this.checkAbort();
                throw ex;
            }
        }
        return l;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            try {
                l = this.wrappedStream.read(b, off, len);
                this.checkEOF(l);
            }
            catch (IOException ex) {
                this.checkAbort();
                throw ex;
            }
        }
        return l;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int available() throws IOException {
        int a = 0;
        if (this.isReadAllowed()) {
            try {
                a = this.wrappedStream.available();
            }
            catch (IOException ex) {
                this.checkAbort();
                throw ex;
            }
        }
        return a;
    }
    
    @Override
    public void close() throws IOException {
        this.selfClosed = true;
        this.checkClose();
    }
    
    protected void checkEOF(final int eof) throws IOException {
        final InputStream toCheckStream = this.wrappedStream;
        if (toCheckStream != null && eof < 0) {
            try {
                boolean scws = true;
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.eofDetected(toCheckStream);
                }
                if (scws) {
                    toCheckStream.close();
                }
            }
            finally {
                this.wrappedStream = null;
            }
        }
    }
    
    protected void checkClose() throws IOException {
        final InputStream toCloseStream = this.wrappedStream;
        if (toCloseStream != null) {
            try {
                boolean scws = true;
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.streamClosed(toCloseStream);
                }
                if (scws) {
                    toCloseStream.close();
                }
            }
            finally {
                this.wrappedStream = null;
            }
        }
    }
    
    protected void checkAbort() throws IOException {
        final InputStream toAbortStream = this.wrappedStream;
        if (toAbortStream != null) {
            try {
                boolean scws = true;
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.streamAbort(toAbortStream);
                }
                if (scws) {
                    toAbortStream.close();
                }
            }
            finally {
                this.wrappedStream = null;
            }
        }
    }
    
    @Override
    public void releaseConnection() throws IOException {
        this.close();
    }
    
    @Override
    public void abortConnection() throws IOException {
        this.selfClosed = true;
        this.checkAbort();
    }
}
