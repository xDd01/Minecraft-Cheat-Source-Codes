package org.apache.http.impl.conn;

import org.apache.http.annotation.*;
import java.util.concurrent.atomic.*;
import org.apache.http.conn.scheme.*;
import org.apache.commons.logging.*;
import org.apache.http.conn.routing.*;
import java.util.concurrent.*;
import org.apache.http.util.*;
import org.apache.http.conn.*;
import org.apache.http.*;
import java.io.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public class BasicClientConnectionManager implements ClientConnectionManager
{
    private final Log log;
    private static final AtomicLong COUNTER;
    public static final String MISUSE_MESSAGE = "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
    private final SchemeRegistry schemeRegistry;
    private final ClientConnectionOperator connOperator;
    private HttpPoolEntry poolEntry;
    private ManagedClientConnectionImpl conn;
    private volatile boolean shutdown;
    
    public BasicClientConnectionManager(final SchemeRegistry schreg) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(schreg, "Scheme registry");
        this.schemeRegistry = schreg;
        this.connOperator = this.createConnectionOperator(schreg);
    }
    
    public BasicClientConnectionManager() {
        this(SchemeRegistryFactory.createDefault());
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.shutdown();
        }
        finally {
            super.finalize();
        }
    }
    
    @Override
    public SchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }
    
    protected ClientConnectionOperator createConnectionOperator(final SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg);
    }
    
    @Override
    public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
        return new ClientConnectionRequest() {
            @Override
            public void abortRequest() {
            }
            
            @Override
            public ManagedClientConnection getConnection(final long timeout, final TimeUnit tunit) {
                return BasicClientConnectionManager.this.getConnection(route, state);
            }
        };
    }
    
    private void assertNotShutdown() {
        Asserts.check(!this.shutdown, "Connection manager has been shut down");
    }
    
    ManagedClientConnection getConnection(final HttpRoute route, final Object state) {
        Args.notNull(route, "Route");
        synchronized (this) {
            this.assertNotShutdown();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Get connection for route " + route);
            }
            Asserts.check(this.conn == null, "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.");
            if (this.poolEntry != null && !this.poolEntry.getPlannedRoute().equals(route)) {
                this.poolEntry.close();
                this.poolEntry = null;
            }
            if (this.poolEntry == null) {
                final String id = Long.toString(BasicClientConnectionManager.COUNTER.getAndIncrement());
                final OperatedClientConnection opconn = this.connOperator.createConnection();
                this.poolEntry = new HttpPoolEntry(this.log, id, route, opconn, 0L, TimeUnit.MILLISECONDS);
            }
            final long now = System.currentTimeMillis();
            if (this.poolEntry.isExpired(now)) {
                this.poolEntry.close();
                this.poolEntry.getTracker().reset();
            }
            return this.conn = new ManagedClientConnectionImpl(this, this.connOperator, this.poolEntry);
        }
    }
    
    private void shutdownConnection(final HttpClientConnection conn) {
        try {
            conn.shutdown();
        }
        catch (IOException iox) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("I/O exception shutting down connection", iox);
            }
        }
    }
    
    @Override
    public void releaseConnection(final ManagedClientConnection conn, final long keepalive, final TimeUnit tunit) {
        Args.check(conn instanceof ManagedClientConnectionImpl, "Connection class mismatch, connection not obtained from this manager");
        final ManagedClientConnectionImpl managedConn = (ManagedClientConnectionImpl)conn;
        synchronized (managedConn) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Releasing connection " + conn);
            }
            if (managedConn.getPoolEntry() == null) {
                return;
            }
            final ClientConnectionManager manager = managedConn.getManager();
            Asserts.check(manager == this, "Connection not obtained from this manager");
            synchronized (this) {
                if (this.shutdown) {
                    this.shutdownConnection(managedConn);
                    return;
                }
                try {
                    if (managedConn.isOpen() && !managedConn.isMarkedReusable()) {
                        this.shutdownConnection(managedConn);
                    }
                    if (managedConn.isMarkedReusable()) {
                        this.poolEntry.updateExpiry(keepalive, (tunit != null) ? tunit : TimeUnit.MILLISECONDS);
                        if (this.log.isDebugEnabled()) {
                            String s;
                            if (keepalive > 0L) {
                                s = "for " + keepalive + " " + tunit;
                            }
                            else {
                                s = "indefinitely";
                            }
                            this.log.debug("Connection can be kept alive " + s);
                        }
                    }
                }
                finally {
                    managedConn.detach();
                    this.conn = null;
                    if (this.poolEntry.isClosed()) {
                        this.poolEntry = null;
                    }
                }
            }
        }
    }
    
    @Override
    public void closeExpiredConnections() {
        synchronized (this) {
            this.assertNotShutdown();
            final long now = System.currentTimeMillis();
            if (this.poolEntry != null && this.poolEntry.isExpired(now)) {
                this.poolEntry.close();
                this.poolEntry.getTracker().reset();
            }
        }
    }
    
    @Override
    public void closeIdleConnections(final long idletime, final TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        synchronized (this) {
            this.assertNotShutdown();
            long time = tunit.toMillis(idletime);
            if (time < 0L) {
                time = 0L;
            }
            final long deadline = System.currentTimeMillis() - time;
            if (this.poolEntry != null && this.poolEntry.getUpdated() <= deadline) {
                this.poolEntry.close();
                this.poolEntry.getTracker().reset();
            }
        }
    }
    
    @Override
    public void shutdown() {
        synchronized (this) {
            this.shutdown = true;
            try {
                if (this.poolEntry != null) {
                    this.poolEntry.close();
                }
            }
            finally {
                this.poolEntry = null;
                this.conn = null;
            }
        }
    }
    
    static {
        COUNTER = new AtomicLong();
    }
}
