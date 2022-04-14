/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.util;

import java.io.Serializable;

public final class DateInterval
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long fromDate;
    private final long toDate;

    public DateInterval(long from, long to2) {
        this.fromDate = from;
        this.toDate = to2;
    }

    public long getFromDate() {
        return this.fromDate;
    }

    public long getToDate() {
        return this.toDate;
    }

    public boolean equals(Object a2) {
        if (a2 instanceof DateInterval) {
            DateInterval di2 = (DateInterval)a2;
            return this.fromDate == di2.fromDate && this.toDate == di2.toDate;
        }
        return false;
    }

    public int hashCode() {
        return (int)(this.fromDate + this.toDate);
    }

    public String toString() {
        return String.valueOf(this.fromDate) + " " + String.valueOf(this.toDate);
    }
}

