package org.apache.http.impl.bootstrap;

import java.util.concurrent.*;
import java.util.*;

class WorkerPoolExecutor extends ThreadPoolExecutor
{
    private final Map<org.apache.http.impl.bootstrap.Worker, Boolean> workerSet;
    
    public WorkerPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.workerSet = new ConcurrentHashMap<org.apache.http.impl.bootstrap.Worker, Boolean>();
    }
    
    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        if (r instanceof org.apache.http.impl.bootstrap.Worker) {
            this.workerSet.put((org.apache.http.impl.bootstrap.Worker)r, Boolean.TRUE);
        }
    }
    
    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        if (r instanceof org.apache.http.impl.bootstrap.Worker) {
            this.workerSet.remove(r);
        }
    }
    
    public Set<org.apache.http.impl.bootstrap.Worker> getWorkers() {
        return new HashSet<org.apache.http.impl.bootstrap.Worker>(this.workerSet.keySet());
    }
}
