package org.apache.http.impl.conn.tsccm;

import org.apache.http.conn.routing.*;
import org.apache.http.conn.params.*;
import org.apache.commons.logging.*;
import java.io.*;
import java.util.*;
import org.apache.http.conn.*;
import org.apache.http.util.*;

@Deprecated
public class RouteSpecificPool
{
    private final Log log;
    protected final HttpRoute route;
    protected final int maxEntries;
    protected final ConnPerRoute connPerRoute;
    protected final LinkedList<BasicPoolEntry> freeEntries;
    protected final Queue<WaitingThread> waitingThreads;
    protected int numEntries;
    
    @Deprecated
    public RouteSpecificPool(final HttpRoute route, final int maxEntries) {
        this.log = LogFactory.getLog(this.getClass());
        this.route = route;
        this.maxEntries = maxEntries;
        this.connPerRoute = new ConnPerRoute() {
            @Override
            public int getMaxForRoute(final HttpRoute unused) {
                return RouteSpecificPool.this.maxEntries;
            }
        };
        this.freeEntries = new LinkedList<BasicPoolEntry>();
        this.waitingThreads = new LinkedList<WaitingThread>();
        this.numEntries = 0;
    }
    
    public RouteSpecificPool(final HttpRoute route, final ConnPerRoute connPerRoute) {
        this.log = LogFactory.getLog(this.getClass());
        this.route = route;
        this.connPerRoute = connPerRoute;
        this.maxEntries = connPerRoute.getMaxForRoute(route);
        this.freeEntries = new LinkedList<BasicPoolEntry>();
        this.waitingThreads = new LinkedList<WaitingThread>();
        this.numEntries = 0;
    }
    
    public final HttpRoute getRoute() {
        return this.route;
    }
    
    public final int getMaxEntries() {
        return this.maxEntries;
    }
    
    public boolean isUnused() {
        return this.numEntries < 1 && this.waitingThreads.isEmpty();
    }
    
    public int getCapacity() {
        return this.connPerRoute.getMaxForRoute(this.route) - this.numEntries;
    }
    
    public final int getEntryCount() {
        return this.numEntries;
    }
    
    public BasicPoolEntry allocEntry(final Object state) {
        if (!this.freeEntries.isEmpty()) {
            final ListIterator<BasicPoolEntry> it = this.freeEntries.listIterator(this.freeEntries.size());
            while (it.hasPrevious()) {
                final BasicPoolEntry entry = it.previous();
                if (entry.getState() == null || LangUtils.equals(state, entry.getState())) {
                    it.remove();
                    return entry;
                }
            }
        }
        if (this.getCapacity() == 0 && !this.freeEntries.isEmpty()) {
            final BasicPoolEntry entry2 = this.freeEntries.remove();
            entry2.shutdownEntry();
            final OperatedClientConnection conn = entry2.getConnection();
            try {
                conn.close();
            }
            catch (IOException ex) {
                this.log.debug("I/O error closing connection", ex);
            }
            return entry2;
        }
        return null;
    }
    
    public void freeEntry(final BasicPoolEntry entry) {
        if (this.numEntries < 1) {
            throw new IllegalStateException("No entry created for this pool. " + this.route);
        }
        if (this.numEntries <= this.freeEntries.size()) {
            throw new IllegalStateException("No entry allocated from this pool. " + this.route);
        }
        this.freeEntries.add(entry);
    }
    
    public void createdEntry(final BasicPoolEntry entry) {
        Args.check(this.route.equals(entry.getPlannedRoute()), "Entry not planned for this pool");
        ++this.numEntries;
    }
    
    public boolean deleteEntry(final BasicPoolEntry entry) {
        final boolean found = this.freeEntries.remove(entry);
        if (found) {
            --this.numEntries;
        }
        return found;
    }
    
    public void dropEntry() {
        Asserts.check(this.numEntries > 0, "There is no entry that could be dropped");
        --this.numEntries;
    }
    
    public void queueThread(final WaitingThread wt) {
        Args.notNull(wt, "Waiting thread");
        this.waitingThreads.add(wt);
    }
    
    public boolean hasThread() {
        return !this.waitingThreads.isEmpty();
    }
    
    public WaitingThread nextThread() {
        return this.waitingThreads.peek();
    }
    
    public void removeThread(final WaitingThread wt) {
        if (wt == null) {
            return;
        }
        this.waitingThreads.remove(wt);
    }
}
