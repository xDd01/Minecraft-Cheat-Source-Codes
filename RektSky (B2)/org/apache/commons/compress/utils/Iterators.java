package org.apache.commons.compress.utils;

import java.util.*;

public class Iterators
{
    public static <T> boolean addAll(final Collection<T> collection, final Iterator<? extends T> iterator) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(iterator);
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= collection.add((T)iterator.next());
        }
        return wasModified;
    }
    
    private Iterators() {
    }
}
