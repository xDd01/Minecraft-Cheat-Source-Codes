package org.apache.http.conn.socket;

import org.apache.http.protocol.*;
import java.io.*;
import org.apache.http.*;
import java.net.*;

public interface ConnectionSocketFactory
{
    Socket createSocket(final HttpContext p0) throws IOException;
    
    Socket connectSocket(final int p0, final Socket p1, final HttpHost p2, final InetSocketAddress p3, final InetSocketAddress p4, final HttpContext p5) throws IOException;
}
