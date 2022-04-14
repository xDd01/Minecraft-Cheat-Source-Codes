package org.apache.http.conn;

import org.apache.http.*;
import java.net.*;
import java.io.*;
import org.apache.http.params.*;

@Deprecated
public interface OperatedClientConnection extends HttpClientConnection, HttpInetConnection
{
    HttpHost getTargetHost();
    
    boolean isSecure();
    
    Socket getSocket();
    
    void opening(final Socket p0, final HttpHost p1) throws IOException;
    
    void openCompleted(final boolean p0, final HttpParams p1) throws IOException;
    
    void update(final Socket p0, final HttpHost p1, final boolean p2, final HttpParams p3) throws IOException;
}
