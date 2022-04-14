package org.apache.http.conn.scheme;

import org.apache.http.params.*;
import java.io.*;
import java.net.*;
import org.apache.http.conn.*;

@Deprecated
public interface SchemeSocketFactory
{
    Socket createSocket(final HttpParams p0) throws IOException;
    
    Socket connectSocket(final Socket p0, final InetSocketAddress p1, final InetSocketAddress p2, final HttpParams p3) throws IOException, UnknownHostException, ConnectTimeoutException;
    
    boolean isSecure(final Socket p0) throws IllegalArgumentException;
}
