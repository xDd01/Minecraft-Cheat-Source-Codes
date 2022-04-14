package org.apache.http.conn.scheme;

import org.apache.http.params.*;
import java.io.*;
import java.net.*;
import org.apache.http.conn.*;

@Deprecated
class SocketFactoryAdaptor implements SocketFactory
{
    private final SchemeSocketFactory factory;
    
    SocketFactoryAdaptor(final SchemeSocketFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public Socket createSocket() throws IOException {
        final HttpParams params = new BasicHttpParams();
        return this.factory.createSocket(params);
    }
    
    @Override
    public Socket connectSocket(final Socket socket, final String host, final int port, final InetAddress localAddress, final int localPort, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        InetSocketAddress local = null;
        if (localAddress != null || localPort > 0) {
            local = new InetSocketAddress(localAddress, (localPort > 0) ? localPort : 0);
        }
        final InetAddress remoteAddress = InetAddress.getByName(host);
        final InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
        return this.factory.connectSocket(socket, remote, local, params);
    }
    
    @Override
    public boolean isSecure(final Socket socket) throws IllegalArgumentException {
        return this.factory.isSecure(socket);
    }
    
    public SchemeSocketFactory getFactory() {
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
        if (obj instanceof SocketFactoryAdaptor) {
            return this.factory.equals(((SocketFactoryAdaptor)obj).factory);
        }
        return this.factory.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return this.factory.hashCode();
    }
}
