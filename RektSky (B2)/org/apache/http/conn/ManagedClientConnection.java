package org.apache.http.conn;

import org.apache.http.conn.routing.*;
import javax.net.ssl.*;
import org.apache.http.protocol.*;
import org.apache.http.params.*;
import java.io.*;
import org.apache.http.*;
import java.util.concurrent.*;

@Deprecated
public interface ManagedClientConnection extends HttpClientConnection, HttpRoutedConnection, ManagedHttpClientConnection, ConnectionReleaseTrigger
{
    boolean isSecure();
    
    HttpRoute getRoute();
    
    SSLSession getSSLSession();
    
    void open(final HttpRoute p0, final HttpContext p1, final HttpParams p2) throws IOException;
    
    void tunnelTarget(final boolean p0, final HttpParams p1) throws IOException;
    
    void tunnelProxy(final HttpHost p0, final boolean p1, final HttpParams p2) throws IOException;
    
    void layerProtocol(final HttpContext p0, final HttpParams p1) throws IOException;
    
    void markReusable();
    
    void unmarkReusable();
    
    boolean isMarkedReusable();
    
    void setState(final Object p0);
    
    Object getState();
    
    void setIdleDuration(final long p0, final TimeUnit p1);
}
