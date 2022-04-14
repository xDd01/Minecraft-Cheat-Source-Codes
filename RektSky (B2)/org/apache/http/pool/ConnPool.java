package org.apache.http.pool;

import org.apache.http.concurrent.*;
import java.util.concurrent.*;

public interface ConnPool<T, E>
{
    Future<E> lease(final T p0, final Object p1, final FutureCallback<E> p2);
    
    void release(final E p0, final boolean p1);
}
