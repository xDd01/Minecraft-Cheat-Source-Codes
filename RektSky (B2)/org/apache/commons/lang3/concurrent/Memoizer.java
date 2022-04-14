package org.apache.commons.lang3.concurrent;

import java.util.concurrent.*;

public class Memoizer<I, O> implements Computable<I, O>
{
    private final ConcurrentMap<I, Future<O>> cache;
    private final Computable<I, O> computable;
    private final boolean recalculate;
    
    public Memoizer(final Computable<I, O> computable) {
        this(computable, false);
    }
    
    public Memoizer(final Computable<I, O> computable, final boolean recalculate) {
        this.cache = new ConcurrentHashMap<I, Future<O>>();
        this.computable = computable;
        this.recalculate = recalculate;
    }
    
    @Override
    public O compute(final I arg) throws InterruptedException {
        while (true) {
            Future<O> future = this.cache.get(arg);
            if (future == null) {
                final Callable<O> eval = new Callable<O>() {
                    @Override
                    public O call() throws InterruptedException {
                        return Memoizer.this.computable.compute(arg);
                    }
                };
                final FutureTask<O> futureTask = new FutureTask<O>(eval);
                future = this.cache.putIfAbsent(arg, futureTask);
                if (future == null) {
                    future = futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();
            }
            catch (CancellationException e2) {
                this.cache.remove(arg, future);
            }
            catch (ExecutionException e) {
                if (this.recalculate) {
                    this.cache.remove(arg, future);
                }
                throw this.launderException(e.getCause());
            }
        }
    }
    
    private RuntimeException launderException(final Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        throw new IllegalStateException("Unchecked exception", throwable);
    }
}
