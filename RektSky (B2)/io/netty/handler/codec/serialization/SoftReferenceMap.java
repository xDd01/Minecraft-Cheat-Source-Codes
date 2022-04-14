package io.netty.handler.codec.serialization;

import java.util.*;
import java.lang.ref.*;

final class SoftReferenceMap<K, V> extends ReferenceMap<K, V>
{
    SoftReferenceMap(final Map<K, Reference<V>> delegate) {
        super(delegate);
    }
    
    @Override
    Reference<V> fold(final V value) {
        return new SoftReference<V>(value);
    }
}
