/*
 * Decompiled with CFR 0.152.
 */
package io.socket.backo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Backoff {
    private long ms = 100L;
    private long max = 10000L;
    private int factor = 2;
    private double jitter;
    private int attempts;

    public long duration() {
        BigInteger ms2 = BigInteger.valueOf(this.ms).multiply(BigInteger.valueOf(this.factor).pow(this.attempts++));
        if (this.jitter != 0.0) {
            double rand = Math.random();
            BigInteger deviation = BigDecimal.valueOf(rand).multiply(BigDecimal.valueOf(this.jitter)).multiply(new BigDecimal(ms2)).toBigInteger();
            ms2 = ((int)Math.floor(rand * 10.0) & 1) == 0 ? ms2.subtract(deviation) : ms2.add(deviation);
        }
        return ms2.min(BigInteger.valueOf(this.max)).longValue();
    }

    public void reset() {
        this.attempts = 0;
    }

    public Backoff setMin(long min) {
        this.ms = min;
        return this;
    }

    public Backoff setMax(long max) {
        this.max = max;
        return this;
    }

    public Backoff setFactor(int factor) {
        this.factor = factor;
        return this;
    }

    public Backoff setJitter(double jitter) {
        this.jitter = jitter;
        return this;
    }

    public int getAttempts() {
        return this.attempts;
    }
}

