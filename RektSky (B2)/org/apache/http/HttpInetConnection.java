package org.apache.http;

import java.net.*;

public interface HttpInetConnection extends HttpConnection
{
    InetAddress getLocalAddress();
    
    int getLocalPort();
    
    InetAddress getRemoteAddress();
    
    int getRemotePort();
}
