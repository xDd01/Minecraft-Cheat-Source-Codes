package org.apache.http.conn;

import org.apache.http.*;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;

public interface ManagedHttpClientConnection extends HttpClientConnection, HttpInetConnection
{
    String getId();
    
    void bind(final Socket p0) throws IOException;
    
    Socket getSocket();
    
    SSLSession getSSLSession();
}
