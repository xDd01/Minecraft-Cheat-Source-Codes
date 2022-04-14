package io.netty.handler.codec.serialization;

import java.util.*;
import java.lang.ref.*;

final class WeakReferenceMap<K, V> extends ReferenceMap<K, V>
{
    WeakReferenceMap(final Map<K, Reference<V>> delegate) {
        super(delegate);
    }
    
    @Override
    Reference<V> fold(final V value) {
        return new WeakReference<V>(value);
    }
}
