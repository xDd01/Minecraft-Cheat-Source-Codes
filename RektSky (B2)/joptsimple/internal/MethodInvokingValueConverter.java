package joptsimple.internal;

import joptsimple.*;
import java.lang.reflect.*;

class MethodInvokingValueConverter<V> implements ValueConverter<V>
{
    private final Method method;
    private final Class<V> clazz;
    
    MethodInvokingValueConverter(final Method method, final Class<V> clazz) {
        this.method = method;
        this.clazz = clazz;
    }
    
    public V convert(final String value) {
        return this.clazz.cast(Reflection.invoke(this.method, value));
    }
    
    public Class<V> valueType() {
        return this.clazz;
    }
    
    public String valuePattern() {
        return null;
    }
}
