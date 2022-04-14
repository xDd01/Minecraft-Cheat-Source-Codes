package org.apache.commons.lang3.mutable;

public class MutableDouble extends Number implements Comparable<MutableDouble>, Mutable<Number>
{
    private static final long serialVersionUID = 1587163916L;
    private double value;
    
    public MutableDouble() {
    }
    
    public MutableDouble(final double value) {
        this.value = value;
    }
    
    public MutableDouble(final Number value) {
        this.value = value.doubleValue();
    }
    
    public MutableDouble(final String value) {
        this.value = Double.parseDouble(value);
    }
    
    @Override
    public Double getValue() {
        return this.value;
    }
    
    public void setValue(final double value) {
        this.value = value;
    }
    
    @Override
    public void setValue(final Number value) {
        this.value = value.doubleValue();
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.value);
    }
    
    public boolean isInfinite() {
        return Double.isInfinite(this.value);
    }
    
    public void increment() {
        ++this.value;
    }
    
    public double getAndIncrement() {
        final double last = this.value;
        ++this.value;
        return last;
    }
    
    public double incrementAndGet() {
        return ++this.value;
    }
    
    public void decrement() {
        --this.value;
    }
    
    public double getAndDecrement() {
        final double last = this.value;
        --this.value;
        return last;
    }
    
    public double decrementAndGet() {
        return --this.value;
    }
    
    public void add(final double operand) {
        this.value += operand;
    }
    
    public void add(final Number operand) {
        this.value += operand.doubleValue();
    }
    
    public void subtract(final double operand) {
        this.value -= operand;
    }
    
    public void subtract(final Number operand) {
        this.value -= operand.doubleValue();
    }
    
    public double addAndGet(final double operand) {
        return this.value += operand;
    }
    
    public double addAndGet(final Number operand) {
        return this.value += operand.doubleValue();
    }
    
    public double getAndAdd(final double operand) {
        final double last = this.value;
        this.value += operand;
        return last;
    }
    
    public double getAndAdd(final Number operand) {
        final double last = this.value;
        this.value += operand.doubleValue();
        return last;
    }
    
    @Override
    public int intValue() {
        return (int)this.value;
    }
    
    @Override
    public long longValue() {
        return (long)this.value;
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    public Double toDouble() {
        return this.doubleValue();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof MutableDouble && Double.doubleToLongBits(((MutableDouble)obj).value) == Double.doubleToLongBits(this.value);
    }
    
    @Override
    public int hashCode() {
        final long bits = Double.doubleToLongBits(this.value);
        return (int)(bits ^ bits >>> 32);
    }
    
    @Override
    public int compareTo(final MutableDouble other) {
        return Double.compare(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
