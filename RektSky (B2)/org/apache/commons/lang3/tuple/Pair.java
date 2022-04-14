package org.apache.commons.lang3.tuple;

import java.io.*;
import org.apache.commons.lang3.builder.*;
import java.util.*;

public abstract class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable
{
    private static final long serialVersionUID = 4954918890077093841L;
    
    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new ImmutablePair<L, R>(left, right);
    }
    
    public abstract L getLeft();
    
    public abstract R getRight();
    
    @Override
    public final L getKey() {
        return this.getLeft();
    }
    
    @Override
    public R getValue() {
        return this.getRight();
    }
    
    @Override
    public int compareTo(final Pair<L, R> other) {
        return new CompareToBuilder().append(this.getLeft(), other.getLeft()).append(this.getRight(), other.getRight()).toComparison();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry) {
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>)obj;
            return Objects.equals(this.getKey(), other.getKey()) && Objects.equals(this.getValue(), other.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
    }
    
    @Override
    public String toString() {
        return "(" + this.getLeft() + ',' + this.getRight() + ')';
    }
    
    public String toString(final String format) {
        return String.format(format, this.getLeft(), this.getRight());
    }
}
