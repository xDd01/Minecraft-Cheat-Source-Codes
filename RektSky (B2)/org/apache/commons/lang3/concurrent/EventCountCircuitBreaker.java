package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.util.*;

public class EventCountCircuitBreaker extends AbstractCircuitBreaker<Integer>
{
    private static final Map<State, StateStrategy> STRATEGY_MAP;
    private final AtomicReference<CheckIntervalData> checkIntervalData;
    private final int openingThreshold;
    private final long openingInterval;
    private final int closingThreshold;
    private final long closingInterval;
    
    public EventCountCircuitBreaker(final int openingThreshold, final long openingInterval, final TimeUnit openingUnit, final int closingThreshold, final long closingInterval, final TimeUnit closingUnit) {
        this.checkIntervalData = new AtomicReference<CheckIntervalData>(new CheckIntervalData(0, 0L));
        this.openingThreshold = openingThreshold;
        this.openingInterval = openingUnit.toNanos(openingInterval);
        this.closingThreshold = closingThreshold;
        this.closingInterval = closingUnit.toNanos(closingInterval);
    }
    
    public EventCountCircuitBreaker(final int openingThreshold, final long checkInterval, final TimeUnit checkUnit, final int closingThreshold) {
        this(openingThreshold, checkInterval, checkUnit, closingThreshold, checkInterval, checkUnit);
    }
    
    public EventCountCircuitBreaker(final int threshold, final long checkInterval, final TimeUnit checkUnit) {
        this(threshold, checkInterval, checkUnit, threshold);
    }
    
    public int getOpeningThreshold() {
        return this.openingThreshold;
    }
    
    public long getOpeningInterval() {
        return this.openingInterval;
    }
    
    public int getClosingThreshold() {
        return this.closingThreshold;
    }
    
    public long getClosingInterval() {
        return this.closingInterval;
    }
    
    @Override
    public boolean checkState() {
        return this.performStateCheck(0);
    }
    
    @Override
    public boolean incrementAndCheckState(final Integer increment) {
        return this.performStateCheck(increment);
    }
    
    public boolean incrementAndCheckState() {
        return this.incrementAndCheckState(1);
    }
    
    @Override
    public void open() {
        super.open();
        this.checkIntervalData.set(new CheckIntervalData(0, this.now()));
    }
    
    @Override
    public void close() {
        super.close();
        this.checkIntervalData.set(new CheckIntervalData(0, this.now()));
    }
    
    private boolean performStateCheck(final int increment) {
        CheckIntervalData currentData;
        CheckIntervalData nextData;
        State currentState;
        do {
            final long time = this.now();
            currentState = this.state.get();
            currentData = this.checkIntervalData.get();
            nextData = this.nextCheckIntervalData(increment, currentData, currentState, time);
        } while (!this.updateCheckIntervalData(currentData, nextData));
        if (stateStrategy(currentState).isStateTransition(this, currentData, nextData)) {
            currentState = currentState.oppositeState();
            this.changeStateAndStartNewCheckInterval(currentState);
        }
        return !AbstractCircuitBreaker.isOpen(currentState);
    }
    
    private boolean updateCheckIntervalData(final CheckIntervalData currentData, final CheckIntervalData nextData) {
        return currentData == nextData || this.checkIntervalData.compareAndSet(currentData, nextData);
    }
    
    private void changeStateAndStartNewCheckInterval(final State newState) {
        this.changeState(newState);
        this.checkIntervalData.set(new CheckIntervalData(0, this.now()));
    }
    
    private CheckIntervalData nextCheckIntervalData(final int increment, final CheckIntervalData currentData, final State currentState, final long time) {
        CheckIntervalData nextData;
        if (stateStrategy(currentState).isCheckIntervalFinished(this, currentData, time)) {
            nextData = new CheckIntervalData(increment, time);
        }
        else {
            nextData = currentData.increment(increment);
        }
        return nextData;
    }
    
    long now() {
        return System.nanoTime();
    }
    
    private static StateStrategy stateStrategy(final State state) {
        return EventCountCircuitBreaker.STRATEGY_MAP.get(state);
    }
    
    private static Map<State, StateStrategy> createStrategyMap() {
        final Map<State, StateStrategy> map = new EnumMap<State, StateStrategy>(State.class);
        map.put(State.CLOSED, new StateStrategyClosed());
        map.put(State.OPEN, new StateStrategyOpen());
        return map;
    }
    
    static {
        STRATEGY_MAP = createStrategyMap();
    }
    
    private static class CheckIntervalData
    {
        private final int eventCount;
        private final long checkIntervalStart;
        
        CheckIntervalData(final int count, final long intervalStart) {
            this.eventCount = count;
            this.checkIntervalStart = intervalStart;
        }
        
        public int getEventCount() {
            return this.eventCount;
        }
        
        public long getCheckIntervalStart() {
            return this.checkIntervalStart;
        }
        
        public CheckIntervalData increment(final int delta) {
            return (delta == 0) ? this : new CheckIntervalData(this.getEventCount() + delta, this.getCheckIntervalStart());
        }
    }
    
    private abstract static class StateStrategy
    {
        public boolean isCheckIntervalFinished(final EventCountCircuitBreaker breaker, final CheckIntervalData currentData, final long now) {
            return now - currentData.getCheckIntervalStart() > this.fetchCheckInterval(breaker);
        }
        
        public abstract boolean isStateTransition(final EventCountCircuitBreaker p0, final CheckIntervalData p1, final CheckIntervalData p2);
        
        protected abstract long fetchCheckInterval(final EventCountCircuitBreaker p0);
    }
    
    private static class StateStrategyClosed extends StateStrategy
    {
        @Override
        public boolean isStateTransition(final EventCountCircuitBreaker breaker, final CheckIntervalData currentData, final CheckIntervalData nextData) {
            return nextData.getEventCount() > breaker.getOpeningThreshold();
        }
        
        @Override
        protected long fetchCheckInterval(final EventCountCircuitBreaker breaker) {
            return breaker.getOpeningInterval();
        }
    }
    
    private static class StateStrategyOpen extends StateStrategy
    {
        @Override
        public boolean isStateTransition(final EventCountCircuitBreaker breaker, final CheckIntervalData currentData, final CheckIntervalData nextData) {
            return nextData.getCheckIntervalStart() != currentData.getCheckIntervalStart() && currentData.getEventCount() < breaker.getClosingThreshold();
        }
        
        @Override
        protected long fetchCheckInterval(final EventCountCircuitBreaker breaker) {
            return breaker.getClosingInterval();
        }
    }
}
