package org.apache.commons.lang3.event;

import org.apache.commons.lang3.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class EventListenerSupport<L> implements Serializable
{
    private static final long serialVersionUID = 3593265990380473632L;
    private List<L> listeners;
    private transient L proxy;
    private transient L[] prototypeArray;
    
    public static <T> EventListenerSupport<T> create(final Class<T> listenerInterface) {
        return new EventListenerSupport<T>(listenerInterface);
    }
    
    public EventListenerSupport(final Class<L> listenerInterface) {
        this(listenerInterface, Thread.currentThread().getContextClassLoader());
    }
    
    public EventListenerSupport(final Class<L> listenerInterface, final ClassLoader classLoader) {
        this();
        Validate.notNull(listenerInterface, "Listener interface cannot be null.", new Object[0]);
        Validate.notNull(classLoader, "ClassLoader cannot be null.", new Object[0]);
        Validate.isTrue(listenerInterface.isInterface(), "Class {0} is not an interface", listenerInterface.getName());
        this.initializeTransientFields(listenerInterface, classLoader);
    }
    
    private EventListenerSupport() {
        this.listeners = new CopyOnWriteArrayList<L>();
    }
    
    public L fire() {
        return this.proxy;
    }
    
    public void addListener(final L listener) {
        this.addListener(listener, true);
    }
    
    public void addListener(final L listener, final boolean allowDuplicate) {
        Validate.notNull(listener, "Listener object cannot be null.", new Object[0]);
        if (allowDuplicate) {
            this.listeners.add(listener);
        }
        else if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    int getListenerCount() {
        return this.listeners.size();
    }
    
    public void removeListener(final L listener) {
        Validate.notNull(listener, "Listener object cannot be null.", new Object[0]);
        this.listeners.remove(listener);
    }
    
    public L[] getListeners() {
        return this.listeners.toArray(this.prototypeArray);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final ArrayList<L> serializableListeners = new ArrayList<L>();
        ObjectOutputStream testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
        for (final L listener : this.listeners) {
            try {
                testObjectOutputStream.writeObject(listener);
                serializableListeners.add(listener);
            }
            catch (IOException exception) {
                testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
            }
        }
        objectOutputStream.writeObject(serializableListeners.toArray(this.prototypeArray));
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final L[] srcListeners = (L[])objectInputStream.readObject();
        this.listeners = new CopyOnWriteArrayList<L>(srcListeners);
        final Class<L> listenerInterface = (Class<L>)srcListeners.getClass().getComponentType();
        this.initializeTransientFields(listenerInterface, Thread.currentThread().getContextClassLoader());
    }
    
    private void initializeTransientFields(final Class<L> listenerInterface, final ClassLoader classLoader) {
        final L[] array = (L[])Array.newInstance(listenerInterface, 0);
        this.prototypeArray = array;
        this.createProxy(listenerInterface, classLoader);
    }
    
    private void createProxy(final Class<L> listenerInterface, final ClassLoader classLoader) {
        this.proxy = listenerInterface.cast(Proxy.newProxyInstance(classLoader, new Class[] { listenerInterface }, this.createInvocationHandler()));
    }
    
    protected InvocationHandler createInvocationHandler() {
        return new ProxyInvocationHandler();
    }
    
    protected class ProxyInvocationHandler implements InvocationHandler
    {
        @Override
        public Object invoke(final Object unusedProxy, final Method method, final Object[] args) throws Throwable {
            for (final L listener : EventListenerSupport.this.listeners) {
                method.invoke(listener, args);
            }
            return null;
        }
    }
}
