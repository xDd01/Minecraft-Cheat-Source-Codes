package org.apache.http.conn;

import org.apache.http.util.*;
import java.io.*;

@Deprecated
public class BasicEofSensorWatcher implements EofSensorWatcher
{
    protected final ManagedClientConnection managedConn;
    protected final boolean attemptReuse;
    
    public BasicEofSensorWatcher(final ManagedClientConnection conn, final boolean reuse) {
        Args.notNull(conn, "Connection");
        this.managedConn = conn;
        this.attemptReuse = reuse;
    }
    
    @Override
    public boolean eofDetected(final InputStream wrapped) throws IOException {
        try {
            if (this.attemptReuse) {
                wrapped.close();
                this.managedConn.markReusable();
            }
        }
        finally {
            this.managedConn.releaseConnection();
        }
        return false;
    }
    
    @Override
    public boolean streamClosed(final InputStream wrapped) throws IOException {
        try {
            if (this.attemptReuse) {
                wrapped.close();
                this.managedConn.markReusable();
            }
        }
        finally {
            this.managedConn.releaseConnection();
        }
        return false;
    }
    
    @Override
    public boolean streamAbort(final InputStream wrapped) throws IOException {
        this.managedConn.abortConnection();
        return false;
    }
}
