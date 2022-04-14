package org.apache.commons.lang3.concurrent;

public abstract class LazyInitializer<T> implements ConcurrentInitializer<T>
{
    private static final Object NO_INIT;
    private volatile T object;
    
    public LazyInitializer() {
        this.object = (T)LazyInitializer.NO_INIT;
    }
    
    @Override
    public T get() throws ConcurrentException {
        T result = this.object;
        if (result == LazyInitializer.NO_INIT) {
            synchronized (this) {
                result = this.object;
                if (result == LazyInitializer.NO_INIT) {
                    result = (this.object = this.initialize());
                }
            }
        }
        return result;
    }
    
    protected abstract T initialize() throws ConcurrentException;
    
    static {
        NO_INIT = new Object();
    }
}
