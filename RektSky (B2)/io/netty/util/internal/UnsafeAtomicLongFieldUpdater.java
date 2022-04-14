package io.netty.util.internal;

import java.util.concurrent.atomic.*;
import sun.misc.*;
import java.lang.reflect.*;

final class UnsafeAtomicLongFieldUpdater<T> extends AtomicLongFieldUpdater<T>
{
    private final long offset;
    private final Unsafe unsafe;
    
    UnsafeAtomicLongFieldUpdater(final Unsafe unsafe, final Class<?> tClass, final String fieldName) throws NoSuchFieldException {
        final Field field = tClass.getDeclaredField(fieldName);
        if (!Modifier.isVolatile(field.getModifiers())) {
            throw new IllegalArgumentException("Must be volatile");
        }
        this.unsafe = unsafe;
        this.offset = unsafe.objectFieldOffset(field);
    }
    
    @Override
    public boolean compareAndSet(final T obj, final long expect, final long update) {
        return this.unsafe.compareAndSwapLong(obj, this.offset, expect, update);
    }
    
    @Override
    public boolean weakCompareAndSet(final T obj, final long expect, final long update) {
        return this.unsafe.compareAndSwapLong(obj, this.offset, expect, update);
    }
    
    @Override
    public void set(final T obj, final long newValue) {
        this.unsafe.putLongVolatile(obj, this.offset, newValue);
    }
    
    @Override
    public void lazySet(final T obj, final long newValue) {
        this.unsafe.putOrderedLong(obj, this.offset, newValue);
    }
    
    @Override
    public long get(final T obj) {
        return this.unsafe.getLongVolatile(obj, this.offset);
    }
}
