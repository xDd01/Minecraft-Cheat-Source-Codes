package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.*;

public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number>
{
    private static final long serialVersionUID = -2135791679L;
    private short value;
    
    public MutableShort() {
    }
    
    public MutableShort(final short value) {
        this.value = value;
    }
    
    public MutableShort(final Number value) {
        this.value = value.shortValue();
    }
    
    public MutableShort(final String value) {
        this.value = Short.parseShort(value);
    }
    
    @Override
    public Short getValue() {
        return this.value;
    }
    
    public void setValue(final short value) {
        this.value = value;
    }
    
    @Override
    public void setValue(final Number value) {
        this.value = value.shortValue();
    }
    
    public void increment() {
        ++this.value;
    }
    
    public short getAndIncrement() {
        final short last = this.value;
        ++this.value;
        return last;
    }
    
    public short incrementAndGet() {
        return (short)(++this.value);
    }
    
    public void decrement() {
        --this.value;
    }
    
    public short getAndDecrement() {
        final short last = this.value;
        --this.value;
        return last;
    }
    
    public short decrementAndGet() {
        return (short)(--this.value);
    }
    
    public void add(final short operand) {
        this.value += operand;
    }
    
    public void add(final Number operand) {
        this.value += operand.shortValue();
    }
    
    public void subtract(final short operand) {
        this.value -= operand;
    }
    
    public void subtract(final Number operand) {
        this.value -= operand.shortValue();
    }
    
    public short addAndGet(final short operand) {
        return this.value += operand;
    }
    
    public short addAndGet(final Number operand) {
        return this.value += operand.shortValue();
    }
    
    public short getAndAdd(final short operand) {
        final short last = this.value;
        this.value += operand;
        return last;
    }
    
    public short getAndAdd(final Number operand) {
        final short last = this.value;
        this.value += operand.shortValue();
        return last;
    }
    
    @Override
    public short shortValue() {
        return this.value;
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
        return this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    public Short toShort() {
        return this.shortValue();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof MutableShort && this.value == ((MutableShort)obj).shortValue();
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int compareTo(final MutableShort other) {
        return NumberUtils.compare(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
