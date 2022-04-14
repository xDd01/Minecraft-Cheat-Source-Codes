package org.apache.http.impl.conn.tsccm;

import org.apache.http.impl.conn.*;
import org.apache.http.conn.routing.*;
import java.lang.ref.*;
import org.apache.http.util.*;
import java.util.concurrent.*;
import org.apache.http.conn.*;

@Deprecated
public class BasicPoolEntry extends AbstractPoolEntry
{
    private final long created;
    private long updated;
    private final long validUntil;
    private long expiry;
    
    public BasicPoolEntry(final ClientConnectionOperator op, final HttpRoute route, final ReferenceQueue<Object> queue) {
        super(op, route);
        Args.notNull(route, "HTTP route");
        this.created = System.currentTimeMillis();
        this.validUntil = Long.MAX_VALUE;
        this.expiry = this.validUntil;
    }
    
    public BasicPoolEntry(final ClientConnectionOperator op, final HttpRoute route) {
        this(op, route, -1L, TimeUnit.MILLISECONDS);
    }
    
    public BasicPoolEntry(final ClientConnectionOperator op, final HttpRoute route, final long connTTL, final TimeUnit timeunit) {
        super(op, route);
        Args.notNull(route, "HTTP route");
        this.created = System.currentTimeMillis();
        if (connTTL > 0L) {
            this.validUntil = this.created + timeunit.toMillis(connTTL);
        }
        else {
            this.validUntil = Long.MAX_VALUE;
        }
        this.expiry = this.validUntil;
    }
    
    protected final OperatedClientConnection getConnection() {
        return super.connection;
    }
    
    protected final HttpRoute getPlannedRoute() {
        return super.route;
    }
    
    protected final BasicPoolEntryRef getWeakRef() {
        return null;
    }
    
    @Override
    protected void shutdownEntry() {
        super.shutdownEntry();
    }
    
    public long getCreated() {
        return this.created;
    }
    
    public long getUpdated() {
        return this.updated;
    }
    
    public long getExpiry() {
        return this.expiry;
    }
    
    public long getValidUntil() {
        return this.validUntil;
    }
    
    public void updateExpiry(final long time, final TimeUnit timeunit) {
        this.updated = System.currentTimeMillis();
        long newExpiry;
        if (time > 0L) {
            newExpiry = this.updated + timeunit.toMillis(time);
        }
        else {
            newExpiry = Long.MAX_VALUE;
        }
        this.expiry = Math.min(this.validUntil, newExpiry);
    }
    
    public boolean isExpired(final long now) {
        return now >= this.expiry;
    }
}
