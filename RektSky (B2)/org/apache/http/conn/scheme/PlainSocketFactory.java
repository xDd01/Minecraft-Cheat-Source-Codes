package org.apache.http.conn.scheme;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import org.apache.http.params.*;
import org.apache.http.conn.*;
import java.io.*;
import java.net.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class PlainSocketFactory implements SocketFactory, SchemeSocketFactory
{
    private final HostNameResolver nameResolver;
    
    public static PlainSocketFactory getSocketFactory() {
        return new PlainSocketFactory();
    }
    
    @Deprecated
    public PlainSocketFactory(final HostNameResolver nameResolver) {
        this.nameResolver = nameResolver;
    }
    
    public PlainSocketFactory() {
        this.nameResolver = null;
    }
    
    @Override
    public Socket createSocket(final HttpParams params) {
        return new Socket();
    }
    
    @Override
    public Socket createSocket() {
        return new Socket();
    }
    
    @Override
    public Socket connectSocket(final Socket socket, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpParams params) throws IOException, ConnectTimeoutException {
        Args.notNull(remoteAddress, "Remote address");
        Args.notNull(params, "HTTP parameters");
        Socket sock = socket;
        if (sock == null) {
            sock = this.createSocket();
        }
        if (localAddress != null) {
            sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
            sock.bind(localAddress);
        }
        final int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        final int soTimeout = HttpConnectionParams.getSoTimeout(params);
        try {
            sock.setSoTimeout(soTimeout);
            sock.connect(remoteAddress, connTimeout);
        }
        catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
        }
        return sock;
    }
    
    @Override
    public final boolean isSecure(final Socket sock) {
        return false;
    }
    
    @Deprecated
    @Override
    public Socket connectSocket(final Socket socket, final String host, final int port, final InetAddress localAddress, final int localPort, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        InetSocketAddress local = null;
        if (localAddress != null || localPort > 0) {
            local = new InetSocketAddress(localAddress, (localPort > 0) ? localPort : 0);
        }
        InetAddress remoteAddress;
        if (this.nameResolver != null) {
            remoteAddress = this.nameResolver.resolve(host);
        }
        else {
            remoteAddress = InetAddress.getByName(host);
        }
        final InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
        return this.connectSocket(socket, remote, local, params);
    }
}
