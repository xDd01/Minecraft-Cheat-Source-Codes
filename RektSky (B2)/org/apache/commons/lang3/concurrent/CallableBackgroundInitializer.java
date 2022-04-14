package org.apache.commons.lang3.concurrent;

import java.util.concurrent.*;
import org.apache.commons.lang3.*;

public class CallableBackgroundInitializer<T> extends BackgroundInitializer<T>
{
    private final Callable<T> callable;
    
    public CallableBackgroundInitializer(final Callable<T> call) {
        this.checkCallable(call);
        this.callable = call;
    }
    
    public CallableBackgroundInitializer(final Callable<T> call, final ExecutorService exec) {
        super(exec);
        this.checkCallable(call);
        this.callable = call;
    }
    
    @Override
    protected T initialize() throws Exception {
        return this.callable.call();
    }
    
    private void checkCallable(final Callable<T> call) {
        Validate.isTrue(call != null, "Callable must not be null!", new Object[0]);
    }
}
