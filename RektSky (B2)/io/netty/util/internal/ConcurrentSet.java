package io.netty.util.internal;

import java.io.*;
import java.util.concurrent.*;
import java.util.*;

public final class ConcurrentSet<E> extends AbstractSet<E> implements Serializable
{
    private static final long serialVersionUID = -6761513279741915432L;
    private final ConcurrentMap<E, Boolean> map;
    
    public ConcurrentSet() {
        this.map = PlatformDependent.newConcurrentHashMap();
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean add(final E o) {
        return this.map.putIfAbsent(o, Boolean.TRUE) == null;
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.map.remove(o) != null;
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}
