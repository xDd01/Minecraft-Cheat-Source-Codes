package org.apache.http.conn.scheme;

import org.apache.http.params.*;
import java.io.*;
import java.net.*;
import org.apache.http.conn.*;

@Deprecated
class SchemeSocketFactoryAdaptor implements SchemeSocketFactory
{
    private final SocketFactory factory;
    
    SchemeSocketFactoryAdaptor(final SocketFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public Socket connectSocket(final Socket sock, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        final String host = remoteAddress.getHostName();
        final int port = remoteAddress.getPort();
        InetAddress local = null;
        int localPort = 0;
        if (localAddress != null) {
            local = localAddress.getAddress();
            localPort = localAddress.getPort();
        }
        return this.factory.connectSocket(sock, host, port, local, localPort, params);
    }
    
    @Override
    public Socket createSocket(final HttpParams params) throws IOException {
        return this.factory.createSocket();
    }
    
    @Override
    public boolean isSecure(final Socket sock) throws IllegalArgumentException {
        return this.factory.isSecure(sock);
    }
    
    public SocketFactory getFactory() {
        return this.factory;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof SchemeSocketFactoryAdaptor) {
            return this.factory.equals(((SchemeSocketFactoryAdaptor)obj).factory);
        }
        return this.factory.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return this.factory.hashCode();
    }
}
