package org.apache.http.conn;

import java.util.concurrent.*;

@Deprecated
public interface ClientConnectionRequest
{
    ManagedClientConnection getConnection(final long p0, final TimeUnit p1) throws InterruptedException, ConnectionPoolTimeoutException;
    
    void abortRequest();
}
