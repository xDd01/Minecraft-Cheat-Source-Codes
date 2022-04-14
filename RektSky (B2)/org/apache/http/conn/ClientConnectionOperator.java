package org.apache.http.conn;

import org.apache.http.*;
import java.net.*;
import org.apache.http.protocol.*;
import org.apache.http.params.*;
import java.io.*;

@Deprecated
public interface ClientConnectionOperator
{
    OperatedClientConnection createConnection();
    
    void openConnection(final OperatedClientConnection p0, final HttpHost p1, final InetAddress p2, final HttpContext p3, final HttpParams p4) throws IOException;
    
    void updateSecureConnection(final OperatedClientConnection p0, final HttpHost p1, final HttpContext p2, final HttpParams p3) throws IOException;
}
