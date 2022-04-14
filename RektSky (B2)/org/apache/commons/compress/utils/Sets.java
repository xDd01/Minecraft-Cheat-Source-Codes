package org.apache.commons.compress.utils;

import java.util.*;

public class Sets
{
    private Sets() {
    }
    
    public static <E> HashSet<E> newHashSet(final E... elements) {
        final HashSet<E> set = new HashSet<E>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
}
