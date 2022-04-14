package org.apache.http.impl.bootstrap;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class ThreadFactoryImpl implements ThreadFactory
{
    private final String namePrefix;
    private final ThreadGroup group;
    private final AtomicLong count;
    
    ThreadFactoryImpl(final String namePrefix, final ThreadGroup group) {
        this.namePrefix = namePrefix;
        this.group = group;
        this.count = new AtomicLong();
    }
    
    ThreadFactoryImpl(final String namePrefix) {
        this(namePrefix, null);
    }
    
    @Override
    public Thread newThread(final Runnable target) {
        return new Thread(this.group, target, this.namePrefix + "-" + this.count.incrementAndGet());
    }
}
