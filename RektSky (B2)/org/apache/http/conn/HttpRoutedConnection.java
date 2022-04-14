package org.apache.http.conn;

import org.apache.http.*;
import org.apache.http.conn.routing.*;
import javax.net.ssl.*;

@Deprecated
public interface HttpRoutedConnection extends HttpInetConnection
{
    boolean isSecure();
    
    HttpRoute getRoute();
    
    SSLSession getSSLSession();
}
