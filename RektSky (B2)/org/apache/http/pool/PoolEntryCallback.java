package org.apache.http.pool;

public interface PoolEntryCallback<T, C>
{
    void process(final PoolEntry<T, C> p0);
}
