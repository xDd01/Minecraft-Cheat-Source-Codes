package org.apache.http.impl.conn;

import org.apache.http.conn.*;
import java.net.*;
import javax.net.ssl.*;
import org.apache.http.protocol.*;
import org.apache.http.params.*;
import org.apache.http.util.*;
import java.io.*;
import org.apache.http.conn.routing.*;
import org.apache.http.*;
import java.util.concurrent.*;
import org.apache.http.pool.*;

@Deprecated
class ManagedClientConnectionImpl implements ManagedClientConnection
{
    private final ClientConnectionManager manager;
    private final ClientConnectionOperator operator;
    private volatile HttpPoolEntry poolEntry;
    private volatile boolean reusable;
    private volatile long duration;
    
    ManagedClientConnectionImpl(final ClientConnectionManager manager, final ClientConnectionOperator operator, final HttpPoolEntry entry) {
        Args.notNull(manager, "Connection manager");
        Args.notNull(operator, "Connection operator");
        Args.notNull(entry, "HTTP pool entry");
        this.manager = manager;
        this.operator = operator;
        this.poolEntry = entry;
        this.reusable = false;
        this.duration = Long.MAX_VALUE;
    }
    
    @Override
    public String getId() {
        return null;
    }
    
    HttpPoolEntry getPoolEntry() {
        return this.poolEntry;
    }
    
    HttpPoolEntry detach() {
        final HttpPoolEntry local = this.poolEntry;
        this.poolEntry = null;
        return local;
    }
    
    public ClientConnectionManager getManager() {
        return this.manager;
    }
    
    private OperatedClientConnection getConnection() {
        final HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            return null;
        }
        return ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
    }
    
    private OperatedClientConnection ensureConnection() {
        final HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            throw new ConnectionShutdownException();
        }
        return ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
    }
    
    private HttpPoolEntry ensurePoolEntry() {
        final HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            throw new ConnectionShutdownException();
        }
        return local;
    }
    
    @Override
    public void close() throws IOException {
        final HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            final OperatedClientConnection conn = ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
            local.getTracker().reset();
            conn.close();
        }
    }
    
    @Override
    public void shutdown() throws IOException {
        final HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            final OperatedClientConnection conn = ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
            local.getTracker().reset();
            conn.shutdown();
        }
    }
    
    @Override
    public boolean isOpen() {
        final OperatedClientConnection conn = this.getConnection();
        return conn != null && conn.isOpen();
    }
    
    @Override
    public boolean isStale() {
        final OperatedClientConnection conn = this.getConnection();
        return conn == null || conn.isStale();
    }
    
    @Override
    public void setSocketTimeout(final int timeout) {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.setSocketTimeout(timeout);
    }
    
    @Override
    public int getSocketTimeout() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getSocketTimeout();
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getMetrics();
    }
    
    @Override
    public void flush() throws IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.flush();
    }
    
    @Override
    public boolean isResponseAvailable(final int timeout) throws IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.isResponseAvailable(timeout);
    }
    
    @Override
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.receiveResponseEntity(response);
    }
    
    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.receiveResponseHeader();
    }
    
    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.sendRequestEntity(request);
    }
    
    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.sendRequestHeader(request);
    }
    
    @Override
    public InetAddress getLocalAddress() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getLocalAddress();
    }
    
    @Override
    public int getLocalPort() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getLocalPort();
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getRemoteAddress();
    }
    
    @Override
    public int getRemotePort() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getRemotePort();
    }
    
    @Override
    public boolean isSecure() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.isSecure();
    }
    
    @Override
    public void bind(final Socket socket) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Socket getSocket() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getSocket();
    }
    
    @Override
    public SSLSession getSSLSession() {
        final OperatedClientConnection conn = this.ensureConnection();
        SSLSession result = null;
        final Socket sock = conn.getSocket();
        if (sock instanceof SSLSocket) {
            result = ((SSLSocket)sock).getSession();
        }
        return result;
    }
    
    public Object getAttribute(final String id) {
        final OperatedClientConnection conn = this.ensureConnection();
        if (conn instanceof HttpContext) {
            return ((HttpContext)conn).getAttribute(id);
        }
        return null;
    }
    
    public Object removeAttribute(final String id) {
        final OperatedClientConnection conn = this.ensureConnection();
        if (conn instanceof HttpContext) {
            return ((HttpContext)conn).removeAttribute(id);
        }
        return null;
    }
    
    public void setAttribute(final String id, final Object obj) {
        final OperatedClientConnection conn = this.ensureConnection();
        if (conn instanceof HttpContext) {
            ((HttpContext)conn).setAttribute(id, obj);
        }
    }
    
    @Override
    public HttpRoute getRoute() {
        final HttpPoolEntry local = this.ensurePoolEntry();
        return local.getEffectiveRoute();
    }
    
    @Override
    public void open(final HttpRoute route, final HttpContext context, final HttpParams params) throws IOException {
        Args.notNull(route, "Route");
        Args.notNull(params, "HTTP parameters");
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(!tracker.isConnected(), "Connection already open");
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        final HttpHost proxy = route.getProxyHost();
        this.operator.openConnection(conn, (proxy != null) ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker2 = this.poolEntry.getTracker();
            if (proxy == null) {
                tracker2.connectTarget(conn.isSecure());
            }
            else {
                tracker2.connectProxy(proxy, conn.isSecure());
            }
        }
    }
    
    @Override
    public void tunnelTarget(final boolean secure, final HttpParams params) throws IOException {
        Args.notNull(params, "HTTP parameters");
        final HttpHost target;
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(tracker.isConnected(), "Connection not open");
            Asserts.check(!tracker.isTunnelled(), "Connection is already tunnelled");
            target = tracker.getTargetHost();
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        conn.update(null, target, secure, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            tracker.tunnelTarget(secure);
        }
    }
    
    @Override
    public void tunnelProxy(final HttpHost next, final boolean secure, final HttpParams params) throws IOException {
        Args.notNull(next, "Next proxy");
        Args.notNull(params, "HTTP parameters");
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(tracker.isConnected(), "Connection not open");
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        conn.update(null, next, secure, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            tracker.tunnelProxy(next, secure);
        }
    }
    
    @Override
    public void layerProtocol(final HttpContext context, final HttpParams params) throws IOException {
        Args.notNull(params, "HTTP parameters");
        final HttpHost target;
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            Asserts.notNull(tracker, "Route tracker");
            Asserts.check(tracker.isConnected(), "Connection not open");
            Asserts.check(tracker.isTunnelled(), "Protocol layering without a tunnel not supported");
            Asserts.check(!tracker.isLayered(), "Multiple protocol layering not supported");
            target = tracker.getTargetHost();
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        this.operator.updateSecureConnection(conn, target, context, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            tracker.layerProtocol(conn.isSecure());
        }
    }
    
    @Override
    public Object getState() {
        final HttpPoolEntry local = this.ensurePoolEntry();
        return local.getState();
    }
    
    @Override
    public void setState(final Object state) {
        final HttpPoolEntry local = this.ensurePoolEntry();
        local.setState(state);
    }
    
    @Override
    public void markReusable() {
        this.reusable = true;
    }
    
    @Override
    public void unmarkReusable() {
        this.reusable = false;
    }
    
    @Override
    public boolean isMarkedReusable() {
        return this.reusable;
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
    public void releaseConnection() {
        synchronized (this) {
            if (this.poolEntry == null) {
                return;
            }
            this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
            this.poolEntry = null;
        }
    }
    
    @Override
    public void abortConnection() {
        synchronized (this) {
            if (this.poolEntry == null) {
                return;
            }
            this.reusable = false;
            final OperatedClientConnection conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
            try {
                conn.shutdown();
            }
            catch (IOException ex) {}
            this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
            this.poolEntry = null;
        }
    }
}
