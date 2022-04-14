package org.apache.http.pool;

import org.apache.http.annotation.*;
import java.util.concurrent.locks.*;
import java.io.*;
import org.apache.http.concurrent.*;
import org.apache.http.util.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public abstract class AbstractConnPool<T, C, E extends PoolEntry<T, C>> implements ConnPool<T, E>, ConnPoolControl<T>
{
    private final Lock lock;
    private final Condition condition;
    private final ConnFactory<T, C> connFactory;
    private final Map<T, RouteSpecificPool<T, C, E>> routeToPool;
    private final Set<E> leased;
    private final LinkedList<E> available;
    private final LinkedList<Future<E>> pending;
    private final Map<T, Integer> maxPerRoute;
    private volatile boolean isShutDown;
    private volatile int defaultMaxPerRoute;
    private volatile int maxTotal;
    private volatile int validateAfterInactivity;
    
    public AbstractConnPool(final ConnFactory<T, C> connFactory, final int defaultMaxPerRoute, final int maxTotal) {
        this.connFactory = Args.notNull(connFactory, "Connection factory");
        this.defaultMaxPerRoute = Args.positive(defaultMaxPerRoute, "Max per route value");
        this.maxTotal = Args.positive(maxTotal, "Max total value");
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.routeToPool = new HashMap<T, RouteSpecificPool<T, C, E>>();
        this.leased = new HashSet<E>();
        this.available = new LinkedList<E>();
        this.pending = new LinkedList<Future<E>>();
        this.maxPerRoute = new HashMap<T, Integer>();
    }
    
    protected abstract E createEntry(final T p0, final C p1);
    
    protected void onLease(final E entry) {
    }
    
    protected void onRelease(final E entry) {
    }
    
    protected void onReuse(final E entry) {
    }
    
    protected boolean validate(final E entry) {
        return true;
    }
    
    public boolean isShutdown() {
        return this.isShutDown;
    }
    
    public void shutdown() throws IOException {
        if (this.isShutDown) {
            return;
        }
        this.isShutDown = true;
        this.lock.lock();
        try {
            for (final E entry : this.available) {
                entry.close();
            }
            for (final E entry : this.leased) {
                entry.close();
            }
            for (final RouteSpecificPool<T, C, E> pool : this.routeToPool.values()) {
                pool.shutdown();
            }
            this.routeToPool.clear();
            this.leased.clear();
            this.available.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private RouteSpecificPool<T, C, E> getPool(final T route) {
        RouteSpecificPool<T, C, E> pool = this.routeToPool.get(route);
        if (pool == null) {
            pool = new RouteSpecificPool<T, C, E>(route) {
                @Override
                protected E createEntry(final C conn) {
                    return AbstractConnPool.this.createEntry(route, conn);
                }
            };
            this.routeToPool.put(route, pool);
        }
        return pool;
    }
    
    @Override
    public Future<E> lease(final T route, final Object state, final FutureCallback<E> callback) {
        Args.notNull(route, "Route");
        Asserts.check(!this.isShutDown, "Connection pool shut down");
        return new Future<E>() {
            private final AtomicBoolean cancelled = new AtomicBoolean(false);
            private final AtomicBoolean done = new AtomicBoolean(false);
            private final AtomicReference<E> entryRef = new AtomicReference<E>(null);
            
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                if (this.cancelled.compareAndSet(false, true)) {
                    this.done.set(true);
                    AbstractConnPool.this.lock.lock();
                    try {
                        AbstractConnPool.this.condition.signalAll();
                    }
                    finally {
                        AbstractConnPool.this.lock.unlock();
                    }
                    if (callback != null) {
                        callback.cancelled();
                    }
                    return true;
                }
                return false;
            }
            
            @Override
            public boolean isCancelled() {
                return this.cancelled.get();
            }
            
            @Override
            public boolean isDone() {
                return this.done.get();
            }
            
            @Override
            public E get() throws InterruptedException, ExecutionException {
                try {
                    return this.get(0L, TimeUnit.MILLISECONDS);
                }
                catch (TimeoutException ex) {
                    throw new ExecutionException(ex);
                }
            }
            
            @Override
            public E get(final long timeout, final TimeUnit tunit) throws InterruptedException, ExecutionException, TimeoutException {
                final E entry = this.entryRef.get();
                if (entry != null) {
                    return entry;
                }
                synchronized (this) {
                    try {
                        E leasedEntry;
                        while (true) {
                            leasedEntry = AbstractConnPool.this.getPoolEntryBlocking(route, state, timeout, tunit, this);
                            if (AbstractConnPool.this.validateAfterInactivity <= 0 || leasedEntry.getUpdated() + AbstractConnPool.this.validateAfterInactivity > System.currentTimeMillis() || AbstractConnPool.this.validate(leasedEntry)) {
                                break;
                            }
                            leasedEntry.close();
                            AbstractConnPool.this.release(leasedEntry, false);
                        }
                        this.entryRef.set(leasedEntry);
                        this.done.set(true);
                        AbstractConnPool.this.onLease(leasedEntry);
                        if (callback != null) {
                            callback.completed(leasedEntry);
                        }
                        return leasedEntry;
                    }
                    catch (IOException ex) {
                        this.done.set(true);
                        if (callback != null) {
                            callback.failed(ex);
                        }
                        throw new ExecutionException(ex);
                    }
                }
            }
        };
    }
    
    public Future<E> lease(final T route, final Object state) {
        return this.lease(route, state, null);
    }
    
    private E getPoolEntryBlocking(final T route, final Object state, final long timeout, final TimeUnit tunit, final Future<E> future) throws IOException, InterruptedException, TimeoutException {
        Date deadline = null;
        if (timeout > 0L) {
            deadline = new Date(System.currentTimeMillis() + tunit.toMillis(timeout));
        }
        this.lock.lock();
        try {
            final RouteSpecificPool<T, C, E> pool = this.getPool(route);
            while (true) {
                Asserts.check(!this.isShutDown, "Connection pool shut down");
                E entry;
                while (true) {
                    entry = pool.getFree(state);
                    if (entry == null) {
                        break;
                    }
                    if (entry.isExpired(System.currentTimeMillis())) {
                        entry.close();
                    }
                    if (!entry.isClosed()) {
                        break;
                    }
                    this.available.remove(entry);
                    pool.free(entry, false);
                }
                if (entry != null) {
                    this.available.remove(entry);
                    this.leased.add(entry);
                    this.onReuse(entry);
                    return entry;
                }
                final int maxPerRoute = this.getMax(route);
                final int excess = Math.max(0, pool.getAllocatedCount() + 1 - maxPerRoute);
                if (excess > 0) {
                    for (int i = 0; i < excess; ++i) {
                        final E lastUsed = pool.getLastUsed();
                        if (lastUsed == null) {
                            break;
                        }
                        lastUsed.close();
                        this.available.remove(lastUsed);
                        pool.remove(lastUsed);
                    }
                }
                if (pool.getAllocatedCount() < maxPerRoute) {
                    final int totalUsed = this.leased.size();
                    final int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);
                    if (freeCapacity > 0) {
                        final int totalAvailable = this.available.size();
                        if (totalAvailable > freeCapacity - 1 && !this.available.isEmpty()) {
                            final E lastUsed2 = this.available.removeLast();
                            lastUsed2.close();
                            final RouteSpecificPool<T, C, E> otherpool = this.getPool(((PoolEntry<T, C>)lastUsed2).getRoute());
                            otherpool.remove(lastUsed2);
                        }
                        final C conn = this.connFactory.create(route);
                        entry = pool.add(conn);
                        this.leased.add(entry);
                        return entry;
                    }
                }
                boolean success = false;
                try {
                    if (future.isCancelled()) {
                        throw new InterruptedException("Operation interrupted");
                    }
                    pool.queue(future);
                    this.pending.add(future);
                    if (deadline != null) {
                        success = this.condition.awaitUntil(deadline);
                    }
                    else {
                        this.condition.await();
                        success = true;
                    }
                    if (future.isCancelled()) {
                        throw new InterruptedException("Operation interrupted");
                    }
                }
                finally {
                    pool.unqueue(future);
                    this.pending.remove(future);
                }
                if (!success && deadline != null && deadline.getTime() <= System.currentTimeMillis()) {
                    throw new TimeoutException("Timeout waiting for connection");
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void release(final E entry, final boolean reusable) {
        this.lock.lock();
        try {
            if (this.leased.remove(entry)) {
                final RouteSpecificPool<T, C, E> pool = this.getPool(((PoolEntry<T, C>)entry).getRoute());
                pool.free(entry, reusable);
                if (reusable && !this.isShutDown) {
                    this.available.addFirst(entry);
                }
                else {
                    entry.close();
                }
                this.onRelease(entry);
                Future<E> future = pool.nextPending();
                if (future != null) {
                    this.pending.remove(future);
                }
                else {
                    future = this.pending.poll();
                }
                if (future != null) {
                    this.condition.signalAll();
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private int getMax(final T route) {
        final Integer v = this.maxPerRoute.get(route);
        if (v != null) {
            return v;
        }
        return this.defaultMaxPerRoute;
    }
    
    @Override
    public void setMaxTotal(final int max) {
        Args.positive(max, "Max value");
        this.lock.lock();
        try {
            this.maxTotal = max;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int getMaxTotal() {
        this.lock.lock();
        try {
            return this.maxTotal;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setDefaultMaxPerRoute(final int max) {
        Args.positive(max, "Max per route value");
        this.lock.lock();
        try {
            this.defaultMaxPerRoute = max;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int getDefaultMaxPerRoute() {
        this.lock.lock();
        try {
            return this.defaultMaxPerRoute;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setMaxPerRoute(final T route, final int max) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            if (max > -1) {
                this.maxPerRoute.put(route, max);
            }
            else {
                this.maxPerRoute.remove(route);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int getMaxPerRoute(final T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            return this.getMax(route);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public PoolStats getTotalStats() {
        this.lock.lock();
        try {
            return new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public PoolStats getStats(final T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            final RouteSpecificPool<T, C, E> pool = this.getPool(route);
            return new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), this.getMax(route));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<T> getRoutes() {
        this.lock.lock();
        try {
            return new HashSet<T>((Collection<? extends T>)this.routeToPool.keySet());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected void enumAvailable(final PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            final Iterator<E> it = this.available.iterator();
            while (it.hasNext()) {
                final E entry = it.next();
                callback.process(entry);
                if (entry.isClosed()) {
                    final RouteSpecificPool<T, C, E> pool = this.getPool(((PoolEntry<T, C>)entry).getRoute());
                    pool.remove(entry);
                    it.remove();
                }
            }
            this.purgePoolMap();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected void enumLeased(final PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            for (final E entry : this.leased) {
                callback.process(entry);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private void purgePoolMap() {
        final Iterator<Map.Entry<T, RouteSpecificPool<T, C, E>>> it = this.routeToPool.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<T, RouteSpecificPool<T, C, E>> entry = it.next();
            final RouteSpecificPool<T, C, E> pool = entry.getValue();
            if (pool.getPendingCount() + pool.getAllocatedCount() == 0) {
                it.remove();
            }
        }
    }
    
    public void closeIdle(final long idletime, final TimeUnit tunit) {
        Args.notNull(tunit, "Time unit");
        long time = tunit.toMillis(idletime);
        if (time < 0L) {
            time = 0L;
        }
        final long deadline = System.currentTimeMillis() - time;
        this.enumAvailable(new PoolEntryCallback<T, C>() {
            @Override
            public void process(final PoolEntry<T, C> entry) {
                if (entry.getUpdated() <= deadline) {
                    entry.close();
                }
            }
        });
    }
    
    public void closeExpired() {
        final long now = System.currentTimeMillis();
        this.enumAvailable(new PoolEntryCallback<T, C>() {
            @Override
            public void process(final PoolEntry<T, C> entry) {
                if (entry.isExpired(now)) {
                    entry.close();
                }
            }
        });
    }
    
    public int getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }
    
    public void setValidateAfterInactivity(final int ms) {
        this.validateAfterInactivity = ms;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[leased: ");
        buffer.append(this.leased);
        buffer.append("][available: ");
        buffer.append(this.available);
        buffer.append("][pending: ");
        buffer.append(this.pending);
        buffer.append("]");
        return buffer.toString();
    }
}
