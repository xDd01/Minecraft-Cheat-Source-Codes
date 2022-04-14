package org.apache.commons.compress.utils;

import java.util.*;

public class ServiceLoaderIterator<E> implements Iterator<E>
{
    private E nextServiceLoader;
    private final Class<E> service;
    private final Iterator<E> serviceLoaderIterator;
    
    public ServiceLoaderIterator(final Class<E> service) {
        this(service, ClassLoader.getSystemClassLoader());
    }
    
    public ServiceLoaderIterator(final Class<E> service, final ClassLoader classLoader) {
        this.service = service;
        final ServiceLoader<E> serviceLoader = ServiceLoader.load(service, classLoader);
        this.serviceLoaderIterator = serviceLoader.iterator();
        this.nextServiceLoader = null;
    }
    
    private boolean getNextServiceLoader() {
        while (this.nextServiceLoader == null) {
            try {
                if (!this.serviceLoaderIterator.hasNext()) {
                    return false;
                }
                this.nextServiceLoader = this.serviceLoaderIterator.next();
                continue;
            }
            catch (ServiceConfigurationError e) {
                if (e.getCause() instanceof SecurityException) {
                    continue;
                }
                throw e;
            }
            break;
        }
        return true;
    }
    
    @Override
    public boolean hasNext() {
        return this.getNextServiceLoader();
    }
    
    @Override
    public E next() {
        if (!this.getNextServiceLoader()) {
            throw new NoSuchElementException("No more elements for service " + this.service.getName());
        }
        final E tempNext = this.nextServiceLoader;
        this.nextServiceLoader = null;
        return tempNext;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("service=" + this.service.getName());
    }
}
