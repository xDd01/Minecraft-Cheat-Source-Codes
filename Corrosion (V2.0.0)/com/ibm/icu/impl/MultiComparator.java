/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiComparator<T>
implements Comparator<T> {
    private Comparator<T>[] comparators;

    public MultiComparator(Comparator<T> ... comparators) {
        this.comparators = comparators;
    }

    @Override
    public int compare(T arg0, T arg1) {
        for (int i2 = 0; i2 < this.comparators.length; ++i2) {
            int result = this.comparators[i2].compare(arg0, arg1);
            if (result == 0) continue;
            if (result > 0) {
                return i2 + 1;
            }
            return -(i2 + 1);
        }
        return 0;
    }
}

