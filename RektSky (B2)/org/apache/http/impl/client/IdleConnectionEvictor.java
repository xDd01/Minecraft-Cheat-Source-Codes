package org.apache.http.impl.client;

import org.apache.http.conn.*;
import java.util.concurrent.*;
import org.apache.http.util.*;

public final class IdleConnectionEvictor
{
    private final HttpClientConnectionManager connectionManager;
    private final ThreadFactory threadFactory;
    private final Thread thread;
    private final long sleepTimeMs;
    private final long maxIdleTimeMs;
    private volatile Exception exception;
    
    public IdleConnectionEvictor(final HttpClientConnectionManager connectionManager, final ThreadFactory threadFactory, final long sleepTime, final TimeUnit sleepTimeUnit, final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        this.connectionManager = Args.notNull(connectionManager, "Connection manager");
        this.threadFactory = ((threadFactory != null) ? threadFactory : new DefaultThreadFactory());
        this.sleepTimeMs = ((sleepTimeUnit != null) ? sleepTimeUnit.toMillis(sleepTime) : sleepTime);
        this.maxIdleTimeMs = ((maxIdleTimeUnit != null) ? maxIdleTimeUnit.toMillis(maxIdleTime) : maxIdleTime);
        this.thread = this.threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(IdleConnectionEvictor.this.sleepTimeMs);
                        connectionManager.closeExpiredConnections();
                        if (IdleConnectionEvictor.this.maxIdleTimeMs > 0L) {
                            connectionManager.closeIdleConnections(IdleConnectionEvictor.this.maxIdleTimeMs, TimeUnit.MILLISECONDS);
                        }
                    }
                }
                catch (Exception ex) {
                    IdleConnectionEvictor.this.exception = ex;
                }
            }
        });
    }
    
    public IdleConnectionEvictor(final HttpClientConnectionManager connectionManager, final long sleepTime, final TimeUnit sleepTimeUnit, final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        this(connectionManager, null, sleepTime, sleepTimeUnit, maxIdleTime, maxIdleTimeUnit);
    }
    
    public IdleConnectionEvictor(final HttpClientConnectionManager connectionManager, final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        this(connectionManager, null, (maxIdleTime > 0L) ? maxIdleTime : 5L, (maxIdleTimeUnit != null) ? maxIdleTimeUnit : TimeUnit.SECONDS, maxIdleTime, maxIdleTimeUnit);
    }
    
    public void start() {
        this.thread.start();
    }
    
    public void shutdown() {
        this.thread.interrupt();
    }
    
    public boolean isRunning() {
        return this.thread.isAlive();
    }
    
    public void awaitTermination(final long time, final TimeUnit tunit) throws InterruptedException {
        this.thread.join(((tunit != null) ? tunit : TimeUnit.MILLISECONDS).toMillis(time));
    }
    
    static class DefaultThreadFactory implements ThreadFactory
    {
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(r, "Connection evictor");
            t.setDaemon(true);
            return t;
        }
    }
}
