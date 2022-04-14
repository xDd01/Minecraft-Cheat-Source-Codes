package org.apache.http.conn.scheme;

import java.io.*;
import org.apache.http.params.*;
import java.net.*;
import org.apache.http.conn.*;

@Deprecated
public interface SocketFactory
{
    Socket createSocket() throws IOException;
    
    Socket connectSocket(final Socket p0, final String p1, final int p2, final InetAddress p3, final int p4, final HttpParams p5) throws IOException, UnknownHostException, ConnectTimeoutException;
    
    boolean isSecure(final Socket p0) throws IllegalArgumentException;
}
