package org.apache.http.conn.scheme;

import org.apache.http.params.*;
import java.io.*;
import java.net.*;
import org.apache.http.conn.*;

@Deprecated
class SchemeLayeredSocketFactoryAdaptor2 implements SchemeLayeredSocketFactory
{
    private final LayeredSchemeSocketFactory factory;
    
    SchemeLayeredSocketFactoryAdaptor2(final LayeredSchemeSocketFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public Socket createSocket(final HttpParams params) throws IOException {
        return this.factory.createSocket(params);
    }
    
    @Override
    public Socket connectSocket(final Socket sock, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        return this.factory.connectSocket(sock, remoteAddress, localAddress, params);
    }
    
    @Override
    public boolean isSecure(final Socket sock) throws IllegalArgumentException {
        return this.factory.isSecure(sock);
    }
    
    @Override
    public Socket createLayeredSocket(final Socket socket, final String target, final int port, final HttpParams params) throws IOException, UnknownHostException {
        return this.factory.createLayeredSocket(socket, target, port, true);
    }
}
