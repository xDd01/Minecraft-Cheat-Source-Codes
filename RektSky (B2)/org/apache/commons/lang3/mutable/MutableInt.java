package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.*;

public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number>
{
    private static final long serialVersionUID = 512176391864L;
    private int value;
    
    public MutableInt() {
    }
    
    public MutableInt(final int value) {
        this.value = value;
    }
    
    public MutableInt(final Number value) {
        this.value = value.intValue();
    }
    
    public MutableInt(final String value) {
        this.value = Integer.parseInt(value);
    }
    
    @Override
    public Integer getValue() {
        return this.value;
    }
    
    public void setValue(final int value) {
        this.value = value;
    }
    
    @Override
    public void setValue(final Number value) {
        this.value = value.intValue();
    }
    
    public void increment() {
        ++this.value;
    }
    
    public int getAndIncrement() {
        final int last = this.value;
        ++this.value;
        return last;
    }
    
    public int incrementAndGet() {
        return ++this.value;
    }
    
    public void decrement() {
        --this.value;
    }
    
    public int getAndDecrement() {
        final int last = this.value;
        --this.value;
        return last;
    }
    
    public int decrementAndGet() {
        return --this.value;
    }
    
    public void add(final int operand) {
        this.value += operand;
    }
    
    public void add(final Number operand) {
        this.value += operand.intValue();
    }
    
    public void subtract(final int operand) {
        this.value -= operand;
    }
    
    public void subtract(final Number operand) {
        this.value -= operand.intValue();
    }
    
    public int addAndGet(final int operand) {
        return this.value += operand;
    }
    
    public int addAndGet(final Number operand) {
        return this.value += operand.intValue();
    }
    
    public int getAndAdd(final int operand) {
        final int last = this.value;
        this.value += operand;
        return last;
    }
    
    public int getAndAdd(final Number operand) {
        final int last = this.value;
        this.value += operand.intValue();
        return last;
    }
    
    @Override
    public int intValue() {
        return this.value;
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    public Integer toInteger() {
        return this.intValue();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof MutableInt && this.value == ((MutableInt)obj).intValue();
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int compareTo(final MutableInt other) {
        return NumberUtils.compare(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
