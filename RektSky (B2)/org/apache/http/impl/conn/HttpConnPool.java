package org.apache.http.impl.conn;

import org.apache.http.conn.routing.*;
import java.util.concurrent.atomic.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import org.apache.http.conn.*;
import org.apache.http.pool.*;
import java.io.*;

@Deprecated
class HttpConnPool extends AbstractConnPool<HttpRoute, OperatedClientConnection, HttpPoolEntry>
{
    private static final AtomicLong COUNTER;
    private final Log log;
    private final long timeToLive;
    private final TimeUnit tunit;
    
    public HttpConnPool(final Log log, final ClientConnectionOperator connOperator, final int defaultMaxPerRoute, final int maxTotal, final long timeToLive, final TimeUnit tunit) {
        super(new InternalConnFactory(connOperator), defaultMaxPerRoute, maxTotal);
        this.log = log;
        this.timeToLive = timeToLive;
        this.tunit = tunit;
    }
    
    @Override
    protected HttpPoolEntry createEntry(final HttpRoute route, final OperatedClientConnection conn) {
        final String id = Long.toString(HttpConnPool.COUNTER.getAndIncrement());
        return new HttpPoolEntry(this.log, id, route, conn, this.timeToLive, this.tunit);
    }
    
    static {
        COUNTER = new AtomicLong();
    }
    
    static class InternalConnFactory implements ConnFactory<HttpRoute, OperatedClientConnection>
    {
        private final ClientConnectionOperator connOperator;
        
        InternalConnFactory(final ClientConnectionOperator connOperator) {
            this.connOperator = connOperator;
        }
        
        @Override
        public OperatedClientConnection create(final HttpRoute route) throws IOException {
            return this.connOperator.createConnection();
        }
    }
}
