package org.apache.commons.lang3.mutable;

public interface Mutable<T>
{
    T getValue();
    
    void setValue(final T p0);
}
