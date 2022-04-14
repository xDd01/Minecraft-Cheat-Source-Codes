package org.apache.http.impl;

import org.apache.http.*;
import org.apache.http.params.*;
import java.io.*;
import org.apache.http.io.*;
import org.apache.http.impl.io.*;
import org.apache.http.util.*;
import java.net.*;

@Deprecated
public class SocketHttpServerConnection extends AbstractHttpServerConnection implements HttpInetConnection
{
    private volatile boolean open;
    private volatile Socket socket;
    
    public SocketHttpServerConnection() {
        this.socket = null;
    }
    
    protected void assertNotOpen() {
        Asserts.check(!this.open, "Connection is already open");
    }
    
    @Override
    protected void assertOpen() {
        Asserts.check(this.open, "Connection is not open");
    }
    
    protected SessionInputBuffer createSessionInputBuffer(final Socket socket, final int buffersize, final HttpParams params) throws IOException {
        return new SocketInputBuffer(socket, buffersize, params);
    }
    
    protected SessionOutputBuffer createSessionOutputBuffer(final Socket socket, final int buffersize, final HttpParams params) throws IOException {
        return new SocketOutputBuffer(socket, buffersize, params);
    }
    
    protected void bind(final Socket socket, final HttpParams params) throws IOException {
        Args.notNull(socket, "Socket");
        Args.notNull(params, "HTTP parameters");
        this.socket = socket;
        final int buffersize = params.getIntParameter("http.socket.buffer-size", -1);
        this.init(this.createSessionInputBuffer(socket, buffersize, params), this.createSessionOutputBuffer(socket, buffersize, params), params);
        this.open = true;
    }
    
    protected Socket getSocket() {
        return this.socket;
    }
    
    @Override
    public boolean isOpen() {
        return this.open;
    }
    
    @Override
    public InetAddress getLocalAddress() {
        if (this.socket != null) {
            return this.socket.getLocalAddress();
        }
        return null;
    }
    
    @Override
    public int getLocalPort() {
        if (this.socket != null) {
            return this.socket.getLocalPort();
        }
        return -1;
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        if (this.socket != null) {
            return this.socket.getInetAddress();
        }
        return null;
    }
    
    @Override
    public int getRemotePort() {
        if (this.socket != null) {
            return this.socket.getPort();
        }
        return -1;
    }
    
    @Override
    public void setSocketTimeout(final int timeout) {
        this.assertOpen();
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
            }
            catch (SocketException ex) {}
        }
    }
    
    @Override
    public int getSocketTimeout() {
        if (this.socket != null) {
            try {
                return this.socket.getSoTimeout();
            }
            catch (SocketException ignore) {
                return -1;
            }
        }
        return -1;
    }
    
    @Override
    public void shutdown() throws IOException {
        this.open = false;
        final Socket tmpsocket = this.socket;
        if (tmpsocket != null) {
            tmpsocket.close();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (!this.open) {
            return;
        }
        this.open = false;
        this.open = false;
        final Socket sock = this.socket;
        try {
            this.doFlush();
            try {
                try {
                    sock.shutdownOutput();
                }
                catch (IOException ex) {}
                try {
                    sock.shutdownInput();
                }
                catch (IOException ex2) {}
            }
            catch (UnsupportedOperationException ex3) {}
        }
        finally {
            sock.close();
        }
    }
    
    private static void formatAddress(final StringBuilder buffer, final SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            final InetSocketAddress addr = (InetSocketAddress)socketAddress;
            buffer.append((addr.getAddress() != null) ? addr.getAddress().getHostAddress() : addr.getAddress()).append(':').append(addr.getPort());
        }
        else {
            buffer.append(socketAddress);
        }
    }
    
    @Override
    public String toString() {
        if (this.socket != null) {
            final StringBuilder buffer = new StringBuilder();
            final SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
            final SocketAddress localAddress = this.socket.getLocalSocketAddress();
            if (remoteAddress != null && localAddress != null) {
                formatAddress(buffer, localAddress);
                buffer.append("<->");
                formatAddress(buffer, remoteAddress);
            }
            return buffer.toString();
        }
        return super.toString();
    }
}
