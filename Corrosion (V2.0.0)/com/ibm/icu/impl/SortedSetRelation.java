/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SortedSetRelation {
    public static final int A_NOT_B = 4;
    public static final int A_AND_B = 2;
    public static final int B_NOT_A = 1;
    public static final int ANY = 7;
    public static final int CONTAINS = 6;
    public static final int DISJOINT = 5;
    public static final int ISCONTAINED = 3;
    public static final int NO_B = 4;
    public static final int EQUALS = 2;
    public static final int NO_A = 1;
    public static final int NONE = 0;
    public static final int ADDALL = 7;
    public static final int A = 6;
    public static final int COMPLEMENTALL = 5;
    public static final int B = 3;
    public static final int REMOVEALL = 4;
    public static final int RETAINALL = 2;
    public static final int B_REMOVEALL = 1;

    public static <T> boolean hasRelation(SortedSet<T> a2, int allow, SortedSet<T> b2) {
        if (allow < 0 || allow > 7) {
            throw new IllegalArgumentException("Relation " + allow + " out of range");
        }
        boolean anb = (allow & 4) != 0;
        boolean ab2 = (allow & 2) != 0;
        boolean bna2 = (allow & 1) != 0;
        switch (allow) {
            case 6: {
                if (a2.size() >= b2.size()) break;
                return false;
            }
            case 3: {
                if (a2.size() <= b2.size()) break;
                return false;
            }
            case 2: {
                if (a2.size() == b2.size()) break;
                return false;
            }
        }
        if (a2.size() == 0) {
            if (b2.size() == 0) {
                return true;
            }
            return bna2;
        }
        if (b2.size() == 0) {
            return anb;
        }
        Iterator ait2 = a2.iterator();
        Iterator bit2 = b2.iterator();
        Object aa2 = ait2.next();
        Object bb2 = bit2.next();
        while (true) {
            int comp;
            if ((comp = ((Comparable)aa2).compareTo(bb2)) == 0) {
                if (!ab2) {
                    return false;
                }
                if (!ait2.hasNext()) {
                    if (!bit2.hasNext()) {
                        return true;
                    }
                    return bna2;
                }
                if (!bit2.hasNext()) {
                    return anb;
                }
                aa2 = ait2.next();
                bb2 = bit2.next();
                continue;
            }
            if (comp < 0) {
                if (!anb) {
                    return false;
                }
                if (!ait2.hasNext()) {
                    return bna2;
                }
                aa2 = ait2.next();
                continue;
            }
            if (!bna2) {
                return false;
            }
            if (!bit2.hasNext()) {
                return anb;
            }
            bb2 = bit2.next();
        }
    }

    public static <T> SortedSet<? extends T> doOperation(SortedSet<T> a2, int relation, SortedSet<T> b2) {
        switch (relation) {
            case 7: {
                a2.addAll(b2);
                return a2;
            }
            case 6: {
                return a2;
            }
            case 3: {
                a2.clear();
                a2.addAll(b2);
                return a2;
            }
            case 4: {
                a2.removeAll(b2);
                return a2;
            }
            case 2: {
                a2.retainAll(b2);
                return a2;
            }
            case 5: {
                TreeSet<T> temp = new TreeSet<T>(b2);
                temp.removeAll(a2);
                a2.removeAll(b2);
                a2.addAll(temp);
                return a2;
            }
            case 1: {
                TreeSet<T> temp = new TreeSet<T>(b2);
                temp.removeAll(a2);
                a2.clear();
                a2.addAll(temp);
                return a2;
            }
            case 0: {
                a2.clear();
                return a2;
            }
        }
        throw new IllegalArgumentException("Relation " + relation + " out of range");
    }
}

