/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.util;

import com.ibm.icu.util.MeasureUnit;

public abstract class Measure {
    private Number number;
    private MeasureUnit unit;

    protected Measure(Number number, MeasureUnit unit) {
        if (number == null || unit == null) {
            throw new NullPointerException();
        }
        this.number = number;
        this.unit = unit;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        try {
            Measure m2 = (Measure)obj;
            return this.unit.equals(m2.unit) && Measure.numbersEqual(this.number, m2.number);
        }
        catch (ClassCastException e2) {
            return false;
        }
    }

    private static boolean numbersEqual(Number a2, Number b2) {
        if (a2.equals(b2)) {
            return true;
        }
        return a2.doubleValue() == b2.doubleValue();
    }

    public int hashCode() {
        return this.number.hashCode() ^ this.unit.hashCode();
    }

    public String toString() {
        return this.number.toString() + ' ' + this.unit.toString();
    }

    public Number getNumber() {
        return this.number;
    }

    public MeasureUnit getUnit() {
        return this.unit;
    }
}

