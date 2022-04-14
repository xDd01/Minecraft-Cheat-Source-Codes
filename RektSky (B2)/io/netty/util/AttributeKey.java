package io.netty.util;

import java.util.concurrent.*;
import io.netty.util.internal.*;

public final class AttributeKey<T> extends UniqueName
{
    private static final ConcurrentMap<String, Boolean> names;
    
    public static <T> AttributeKey<T> valueOf(final String name) {
        return new AttributeKey<T>(name);
    }
    
    @Deprecated
    public AttributeKey(final String name) {
        super(AttributeKey.names, name, new Object[0]);
    }
    
    static {
        names = PlatformDependent.newConcurrentHashMap();
    }
}
