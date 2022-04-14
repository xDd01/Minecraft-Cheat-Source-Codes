package org.apache.http.impl.conn;

import org.apache.http.protocol.*;
import org.apache.http.conn.*;
import java.io.*;
import org.apache.http.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.concurrent.*;

@Deprecated
public abstract class AbstractClientConnAdapter implements ManagedClientConnection, HttpContext
{
    private final ClientConnectionManager connManager;
    private volatile OperatedClientConnection wrappedConnection;
    private volatile boolean markedReusable;
    private volatile boolean released;
    private volatile long duration;
    
    protected AbstractClientConnAdapter(final ClientConnectionManager mgr, final OperatedClientConnection conn) {
        this.connManager = mgr;
        this.wrappedConnection = conn;
        this.markedReusable = false;
        this.released = false;
        this.duration = Long.MAX_VALUE;
    }
    
    protected synchronized void detach() {
        this.wrappedConnection = null;
        this.duration = Long.MAX_VALUE;
    }
    
    protected OperatedClientConnection getWrappedConnection() {
        return this.wrappedConnection;
    }
    
    protected ClientConnectionManager getManager() {
        return this.connManager;
    }
    
    @Deprecated
    protected final void assertNotAborted() throws InterruptedIOException {
        if (this.isReleased()) {
            throw new InterruptedIOException("Connection has been shut down");
        }
    }
    
    protected boolean isReleased() {
        return this.released;
    }
    
    protected final void assertValid(final OperatedClientConnection wrappedConn) throws ConnectionShutdownException {
        if (this.isReleased() || wrappedConn == null) {
            throw new ConnectionShutdownException();
        }
    }
    
    @Override
    public boolean isOpen() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        return conn != null && conn.isOpen();
    }
    
    @Override
    public boolean isStale() {
        if (this.isReleased()) {
            return true;
        }
        final OperatedClientConnection conn = this.getWrappedConnection();
        return conn == null || conn.isStale();
    }
    
    @Override
    public void setSocketTimeout(final int timeout) {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        conn.setSocketTimeout(timeout);
    }
    
    @Override
    public int getSocketTimeout() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.getSocketTimeout();
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.getMetrics();
    }
    
    @Override
    public void flush() throws IOException {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        conn.flush();
    }
    
    @Override
    public boolean isResponseAvailable(final int timeout) throws IOException {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.isResponseAvailable(timeout);
    }
    
    @Override
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        this.unmarkReusable();
        conn.receiveResponseEntity(response);
    }
    
    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        this.unmarkReusable();
        return conn.receiveResponseHeader();
    }
    
    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        this.unmarkReusable();
        conn.sendRequestEntity(request);
    }
    
    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        this.unmarkReusable();
        conn.sendRequestHeader(request);
    }
    
    @Override
    public InetAddress getLocalAddress() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.getLocalAddress();
    }
    
    @Override
    public int getLocalPort() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.getLocalPort();
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.getRemoteAddress();
    }
    
    @Override
    public int getRemotePort() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.getRemotePort();
    }
    
    @Override
    public boolean isSecure() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        return conn.isSecure();
    }
    
    @Override
    public void bind(final Socket socket) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Socket getSocket() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        if (!this.isOpen()) {
            return null;
        }
        return conn.getSocket();
    }
    
    @Override
    public SSLSession getSSLSession() {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        if (!this.isOpen()) {
            return null;
        }
        SSLSession result = null;
        final Socket sock = conn.getSocket();
        if (sock instanceof SSLSocket) {
            result = ((SSLSocket)sock).getSession();
        }
        return result;
    }
    
    @Override
    public void markReusable() {
        this.markedReusable = true;
    }
    
    @Override
    public void unmarkReusable() {
        this.markedReusable = false;
    }
    
    @Override
    public boolean isMarkedReusable() {
        return this.markedReusable;
    }
    
    @Override
    public void setIdleDuration(final long duration, final TimeUnit unit) {
        if (duration > 0L) {
            this.duration = unit.toMillis(duration);
        }
        else {
            this.duration = -1L;
        }
    }
    
    @Override
    public synchronized void releaseConnection() {
        if (this.released) {
            return;
        }
        this.released = true;
        this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public synchronized void abortConnection() {
        if (this.released) {
            return;
        }
        this.released = true;
        this.unmarkReusable();
        try {
            this.shutdown();
        }
        catch (IOException ex) {}
        this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public Object getAttribute(final String id) {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        if (conn instanceof HttpContext) {
            return ((HttpContext)conn).getAttribute(id);
        }
        return null;
    }
    
    @Override
    public Object removeAttribute(final String id) {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        if (conn instanceof HttpContext) {
            return ((HttpContext)conn).removeAttribute(id);
        }
        return null;
    }
    
    @Override
    public void setAttribute(final String id, final Object obj) {
        final OperatedClientConnection conn = this.getWrappedConnection();
        this.assertValid(conn);
        if (conn instanceof HttpContext) {
            ((HttpContext)conn).setAttribute(id, obj);
        }
    }
}
