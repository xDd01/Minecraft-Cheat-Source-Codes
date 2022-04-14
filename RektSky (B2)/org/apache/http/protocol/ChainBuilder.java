package org.apache.http.protocol;

import java.util.*;

final class ChainBuilder<E>
{
    private final LinkedList<E> list;
    private final Map<Class<?>, E> uniqueClasses;
    
    public ChainBuilder() {
        this.list = new LinkedList<E>();
        this.uniqueClasses = new HashMap<Class<?>, E>();
    }
    
    private void ensureUnique(final E e) {
        final E previous = this.uniqueClasses.remove(e.getClass());
        if (previous != null) {
            this.list.remove(previous);
        }
        this.uniqueClasses.put(e.getClass(), e);
    }
    
    public ChainBuilder<E> addFirst(final E e) {
        if (e == null) {
            return this;
        }
        this.ensureUnique(e);
        this.list.addFirst(e);
        return this;
    }
    
    public ChainBuilder<E> addLast(final E e) {
        if (e == null) {
            return this;
        }
        this.ensureUnique(e);
        this.list.addLast(e);
        return this;
    }
    
    public ChainBuilder<E> addAllFirst(final Collection<E> c) {
        if (c == null) {
            return this;
        }
        for (final E e : c) {
            this.addFirst(e);
        }
        return this;
    }
    
    public ChainBuilder<E> addAllFirst(final E... c) {
        if (c == null) {
            return this;
        }
        for (final E e : c) {
            this.addFirst(e);
        }
        return this;
    }
    
    public ChainBuilder<E> addAllLast(final Collection<E> c) {
        if (c == null) {
            return this;
        }
        for (final E e : c) {
            this.addLast(e);
        }
        return this;
    }
    
    public ChainBuilder<E> addAllLast(final E... c) {
        if (c == null) {
            return this;
        }
        for (final E e : c) {
            this.addLast(e);
        }
        return this;
    }
    
    public LinkedList<E> build() {
        return new LinkedList<E>((Collection<? extends E>)this.list);
    }
}
