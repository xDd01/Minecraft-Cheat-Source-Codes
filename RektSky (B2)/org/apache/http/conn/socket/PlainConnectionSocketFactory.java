package org.apache.http.conn.socket;

import org.apache.http.annotation.*;
import org.apache.http.protocol.*;
import java.io.*;
import org.apache.http.*;
import java.net.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class PlainConnectionSocketFactory implements ConnectionSocketFactory
{
    public static final PlainConnectionSocketFactory INSTANCE;
    
    public static PlainConnectionSocketFactory getSocketFactory() {
        return PlainConnectionSocketFactory.INSTANCE;
    }
    
    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        return new Socket();
    }
    
    @Override
    public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException {
        final Socket sock = (socket != null) ? socket : this.createSocket(context);
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        try {
            sock.connect(remoteAddress, connectTimeout);
        }
        catch (IOException ex) {
            try {
                sock.close();
            }
            catch (IOException ex2) {}
            throw ex;
        }
        return sock;
    }
    
    static {
        INSTANCE = new PlainConnectionSocketFactory();
    }
}
