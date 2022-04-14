package org.apache.http.client.utils;

import org.apache.http.annotation.*;
import java.lang.reflect.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class JdkIdn implements Idn
{
    private final Method toUnicode;
    
    public JdkIdn() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("java.net.IDN");
        try {
            this.toUnicode = clazz.getMethod("toUnicode", String.class);
        }
        catch (SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        catch (NoSuchMethodException e2) {
            throw new IllegalStateException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public String toUnicode(final String punycode) {
        try {
            return (String)this.toUnicode.invoke(null, punycode);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        catch (InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            throw new RuntimeException(t.getMessage(), t);
        }
    }
}
