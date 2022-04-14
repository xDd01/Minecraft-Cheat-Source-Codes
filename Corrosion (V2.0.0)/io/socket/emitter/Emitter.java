/*
 * Decompiled with CFR 0.152.
 */
package io.socket.emitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class Emitter {
    private ConcurrentMap<String, ConcurrentLinkedQueue<Listener>> callbacks = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Listener>>();

    public Emitter on(String event, Listener fn2) {
        ConcurrentLinkedQueue tempCallbacks;
        ConcurrentLinkedQueue<Listener> callbacks = (ConcurrentLinkedQueue<Listener>)this.callbacks.get(event);
        if (callbacks == null && (tempCallbacks = this.callbacks.putIfAbsent(event, callbacks = new ConcurrentLinkedQueue<Listener>())) != null) {
            callbacks = tempCallbacks;
        }
        callbacks.add(fn2);
        return this;
    }

    public Emitter once(String event, Listener fn2) {
        this.on(event, new OnceListener(event, fn2));
        return this;
    }

    public Emitter off() {
        this.callbacks.clear();
        return this;
    }

    public Emitter off(String event) {
        this.callbacks.remove(event);
        return this;
    }

    public Emitter off(String event, Listener fn2) {
        ConcurrentLinkedQueue callbacks = (ConcurrentLinkedQueue)this.callbacks.get(event);
        if (callbacks != null) {
            Iterator it2 = callbacks.iterator();
            while (it2.hasNext()) {
                Listener internal = (Listener)it2.next();
                if (!Emitter.sameAs(fn2, internal)) continue;
                it2.remove();
                break;
            }
        }
        return this;
    }

    private static boolean sameAs(Listener fn2, Listener internal) {
        if (fn2.equals(internal)) {
            return true;
        }
        if (internal instanceof OnceListener) {
            return fn2.equals(((OnceListener)internal).fn);
        }
        return false;
    }

    public Emitter emit(String event, Object ... args) {
        ConcurrentLinkedQueue callbacks = (ConcurrentLinkedQueue)this.callbacks.get(event);
        if (callbacks != null) {
            for (Listener fn2 : callbacks) {
                fn2.call(args);
            }
        }
        return this;
    }

    public List<Listener> listeners(String event) {
        ConcurrentLinkedQueue callbacks = (ConcurrentLinkedQueue)this.callbacks.get(event);
        return callbacks != null ? new ArrayList<Listener>(callbacks) : new ArrayList(0);
    }

    public boolean hasListeners(String event) {
        ConcurrentLinkedQueue callbacks = (ConcurrentLinkedQueue)this.callbacks.get(event);
        return callbacks != null && !callbacks.isEmpty();
    }

    private class OnceListener
    implements Listener {
        public final String event;
        public final Listener fn;

        public OnceListener(String event, Listener fn2) {
            this.event = event;
            this.fn = fn2;
        }

        @Override
        public void call(Object ... args) {
            Emitter.this.off(this.event, this);
            this.fn.call(args);
        }
    }

    public static interface Listener {
        public void call(Object ... var1);
    }
}

