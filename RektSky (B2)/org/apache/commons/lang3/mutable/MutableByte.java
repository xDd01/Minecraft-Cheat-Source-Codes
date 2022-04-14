package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.*;

public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number>
{
    private static final long serialVersionUID = -1585823265L;
    private byte value;
    
    public MutableByte() {
    }
    
    public MutableByte(final byte value) {
        this.value = value;
    }
    
    public MutableByte(final Number value) {
        this.value = value.byteValue();
    }
    
    public MutableByte(final String value) {
        this.value = Byte.parseByte(value);
    }
    
    @Override
    public Byte getValue() {
        return this.value;
    }
    
    public void setValue(final byte value) {
        this.value = value;
    }
    
    @Override
    public void setValue(final Number value) {
        this.value = value.byteValue();
    }
    
    public void increment() {
        ++this.value;
    }
    
    public byte getAndIncrement() {
        final byte last = this.value;
        ++this.value;
        return last;
    }
    
    public byte incrementAndGet() {
        return (byte)(++this.value);
    }
    
    public void decrement() {
        --this.value;
    }
    
    public byte getAndDecrement() {
        final byte last = this.value;
        --this.value;
        return last;
    }
    
    public byte decrementAndGet() {
        return (byte)(--this.value);
    }
    
    public void add(final byte operand) {
        this.value += operand;
    }
    
    public void add(final Number operand) {
        this.value += operand.byteValue();
    }
    
    public void subtract(final byte operand) {
        this.value -= operand;
    }
    
    public void subtract(final Number operand) {
        this.value -= operand.byteValue();
    }
    
    public byte addAndGet(final byte operand) {
        return this.value += operand;
    }
    
    public byte addAndGet(final Number operand) {
        return this.value += operand.byteValue();
    }
    
    public byte getAndAdd(final byte operand) {
        final byte last = this.value;
        this.value += operand;
        return last;
    }
    
    public byte getAndAdd(final Number operand) {
        final byte last = this.value;
        this.value += operand.byteValue();
        return last;
    }
    
    @Override
    public byte byteValue() {
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
    
    public Byte toByte() {
        return this.byteValue();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof MutableByte && this.value == ((MutableByte)obj).byteValue();
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int compareTo(final MutableByte other) {
        return NumberUtils.compare(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
