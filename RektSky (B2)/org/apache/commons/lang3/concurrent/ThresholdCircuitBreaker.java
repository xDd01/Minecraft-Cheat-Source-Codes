package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.*;

public class ThresholdCircuitBreaker extends AbstractCircuitBreaker<Long>
{
    private static final long INITIAL_COUNT = 0L;
    private final long threshold;
    private final AtomicLong used;
    
    public ThresholdCircuitBreaker(final long threshold) {
        this.used = new AtomicLong(0L);
        this.threshold = threshold;
    }
    
    public long getThreshold() {
        return this.threshold;
    }
    
    @Override
    public boolean checkState() {
        return this.isOpen();
    }
    
    @Override
    public void close() {
        super.close();
        this.used.set(0L);
    }
    
    @Override
    public boolean incrementAndCheckState(final Long increment) {
        if (this.threshold == 0L) {
            this.open();
        }
        final long used = this.used.addAndGet(increment);
        if (used > this.threshold) {
            this.open();
        }
        return this.checkState();
    }
}
