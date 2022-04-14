package org.apache.http.conn;

import org.apache.http.conn.routing.*;
import org.apache.http.*;
import java.util.concurrent.*;
import org.apache.http.protocol.*;
import java.io.*;

public interface HttpClientConnectionManager
{
    ConnectionRequest requestConnection(final HttpRoute p0, final Object p1);
    
    void releaseConnection(final HttpClientConnection p0, final Object p1, final long p2, final TimeUnit p3);
    
    void connect(final HttpClientConnection p0, final HttpRoute p1, final int p2, final HttpContext p3) throws IOException;
    
    void upgrade(final HttpClientConnection p0, final HttpRoute p1, final HttpContext p2) throws IOException;
    
    void routeComplete(final HttpClientConnection p0, final HttpRoute p1, final HttpContext p2) throws IOException;
    
    void closeIdleConnections(final long p0, final TimeUnit p1);
    
    void closeExpiredConnections();
    
    void shutdown();
}
