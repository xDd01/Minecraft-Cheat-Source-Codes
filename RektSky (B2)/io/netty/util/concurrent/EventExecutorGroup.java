package io.netty.util.concurrent;

import java.util.*;
import java.util.concurrent.*;

public interface EventExecutorGroup extends ScheduledExecutorService, Iterable<EventExecutor>
{
    boolean isShuttingDown();
    
    Future<?> shutdownGracefully();
    
    Future<?> shutdownGracefully(final long p0, final long p1, final TimeUnit p2);
    
    Future<?> terminationFuture();
    
    @Deprecated
    void shutdown();
    
    @Deprecated
    List<Runnable> shutdownNow();
    
    EventExecutor next();
    
    Iterator<EventExecutor> iterator();
    
    Future<?> submit(final Runnable p0);
    
     <T> Future<T> submit(final Runnable p0, final T p1);
    
     <T> Future<T> submit(final Callable<T> p0);
    
    ScheduledFuture<?> schedule(final Runnable p0, final long p1, final TimeUnit p2);
    
     <V> ScheduledFuture<V> schedule(final Callable<V> p0, final long p1, final TimeUnit p2);
    
    ScheduledFuture<?> scheduleAtFixedRate(final Runnable p0, final long p1, final long p2, final TimeUnit p3);
    
    ScheduledFuture<?> scheduleWithFixedDelay(final Runnable p0, final long p1, final long p2, final TimeUnit p3);
}
