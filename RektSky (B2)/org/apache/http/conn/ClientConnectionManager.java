package org.apache.http.conn;

import org.apache.http.conn.scheme.*;
import org.apache.http.conn.routing.*;
import java.util.concurrent.*;

@Deprecated
public interface ClientConnectionManager
{
    SchemeRegistry getSchemeRegistry();
    
    ClientConnectionRequest requestConnection(final HttpRoute p0, final Object p1);
    
    void releaseConnection(final ManagedClientConnection p0, final long p1, final TimeUnit p2);
    
    void closeIdleConnections(final long p0, final TimeUnit p1);
    
    void closeExpiredConnections();
    
    void shutdown();
}
