/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.utils.Idn;

@Immutable
public class JdkIdn
implements Idn {
    private final Method toUnicode;

    public JdkIdn() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("java.net.IDN");
        try {
            this.toUnicode = clazz.getMethod("toUnicode", String.class);
        }
        catch (SecurityException e2) {
            throw new IllegalStateException(e2.getMessage(), e2);
        }
        catch (NoSuchMethodException e3) {
            throw new IllegalStateException(e3.getMessage(), e3);
        }
    }

    public String toUnicode(String punycode) {
        try {
            return (String)this.toUnicode.invoke(null, punycode);
        }
        catch (IllegalAccessException e2) {
            throw new IllegalStateException(e2.getMessage(), e2);
        }
        catch (InvocationTargetException e3) {
            Throwable t2 = e3.getCause();
            throw new RuntimeException(t2.getMessage(), t2);
        }
    }
}

