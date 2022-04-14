package org.apache.http.conn;

import org.apache.http.*;
import java.net.*;
import org.apache.http.config.*;
import org.apache.http.protocol.*;
import java.io.*;

public interface HttpClientConnectionOperator
{
    void connect(final ManagedHttpClientConnection p0, final HttpHost p1, final InetSocketAddress p2, final int p3, final SocketConfig p4, final HttpContext p5) throws IOException;
    
    void upgrade(final ManagedHttpClientConnection p0, final HttpHost p1, final HttpContext p2) throws IOException;
}
