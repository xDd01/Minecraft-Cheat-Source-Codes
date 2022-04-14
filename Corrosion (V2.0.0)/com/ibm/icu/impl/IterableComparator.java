/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import java.util.Comparator;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IterableComparator<T>
implements Comparator<Iterable<T>> {
    private final Comparator<T> comparator;
    private final int shorterFirst;
    private static final IterableComparator NOCOMPARATOR = new IterableComparator();

    public IterableComparator() {
        this(null, true);
    }

    public IterableComparator(Comparator<T> comparator) {
        this(comparator, true);
    }

    public IterableComparator(Comparator<T> comparator, boolean shorterFirst) {
        this.comparator = comparator;
        this.shorterFirst = shorterFirst ? 1 : -1;
    }

    @Override
    public int compare(Iterable<T> a2, Iterable<T> b2) {
        T bItem;
        T aItem;
        int result;
        if (a2 == null) {
            return b2 == null ? 0 : -this.shorterFirst;
        }
        if (b2 == null) {
            return this.shorterFirst;
        }
        Iterator<T> ai2 = a2.iterator();
        Iterator<T> bi2 = b2.iterator();
        do {
            if (!ai2.hasNext()) {
                return bi2.hasNext() ? -this.shorterFirst : 0;
            }
            if (!bi2.hasNext()) {
                return this.shorterFirst;
            }
            aItem = ai2.next();
            bItem = bi2.next();
        } while ((result = this.comparator != null ? this.comparator.compare(aItem, bItem) : ((Comparable)aItem).compareTo(bItem)) == 0);
        return result;
    }

    public static <T> int compareIterables(Iterable<T> a2, Iterable<T> b2) {
        return NOCOMPARATOR.compare(a2, b2);
    }
}

