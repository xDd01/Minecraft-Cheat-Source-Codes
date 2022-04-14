/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.execchain;

import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
class RequestEntityExecHandler
implements InvocationHandler {
    private static final Method WRITE_TO_METHOD;
    private final HttpEntity original;
    private boolean consumed = false;

    RequestEntityExecHandler(HttpEntity original) {
        this.original = original;
    }

    public HttpEntity getOriginal() {
        return this.original;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (method.equals(WRITE_TO_METHOD)) {
                this.consumed = true;
            }
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
            WRITE_TO_METHOD = HttpEntity.class.getMethod("writeTo", OutputStream.class);
        }
        catch (NoSuchMethodException ex2) {
            throw new Error(ex2);
        }
    }
}

