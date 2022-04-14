package org.apache.commons.lang3.builder;

public interface Diffable<T>
{
    DiffResult diff(final T p0);
}
