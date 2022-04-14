/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.execchain;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.execchain.ConnectionHolder;
import org.apache.http.impl.execchain.ResponseEntityWrapper;

@NotThreadSafe
class ResponseProxyHandler
implements InvocationHandler {
    private static final Method CLOSE_METHOD;
    private final HttpResponse original;
    private final ConnectionHolder connHolder;

    ResponseProxyHandler(HttpResponse original, ConnectionHolder connHolder) {
        this.original = original;
        this.connHolder = connHolder;
        HttpEntity entity = original.getEntity();
        if (entity != null && entity.isStreaming() && connHolder != null) {
            this.original.setEntity(new ResponseEntityWrapper(entity, connHolder));
        }
    }

    public void close() throws IOException {
        if (this.connHolder != null) {
            this.connHolder.abortConnection();
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(CLOSE_METHOD)) {
            this.close();
            return null;
        }
        try {
            return method.invoke(this.original, args);
        }
        catch (InvocationTargetException ex2) {
            Throwable cause = ex2.getCause();
            if (cause != null) {
                throw cause;
            }
            throw ex2;
        }
    }

    static {
        try {
            CLOSE_METHOD = Closeable.class.getMethod("close", new Class[0]);
        }
        catch (NoSuchMethodException ex2) {
            throw new Error(ex2);
        }
    }
}

