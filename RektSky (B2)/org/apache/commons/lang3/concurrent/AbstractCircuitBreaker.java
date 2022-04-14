package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.*;
import java.beans.*;

public abstract class AbstractCircuitBreaker<T> implements CircuitBreaker<T>
{
    public static final String PROPERTY_NAME = "open";
    protected final AtomicReference<State> state;
    private final PropertyChangeSupport changeSupport;
    
    public AbstractCircuitBreaker() {
        this.state = new AtomicReference<State>(State.CLOSED);
        this.changeSupport = new PropertyChangeSupport(this);
    }
    
    @Override
    public boolean isOpen() {
        return isOpen(this.state.get());
    }
    
    @Override
    public boolean isClosed() {
        return !this.isOpen();
    }
    
    @Override
    public abstract boolean checkState();
    
    @Override
    public abstract boolean incrementAndCheckState(final T p0);
    
    @Override
    public void close() {
        this.changeState(State.CLOSED);
    }
    
    @Override
    public void open() {
        this.changeState(State.OPEN);
    }
    
    protected static boolean isOpen(final State state) {
        return state == State.OPEN;
    }
    
    protected void changeState(final State newState) {
        if (this.state.compareAndSet(newState.oppositeState(), newState)) {
            this.changeSupport.firePropertyChange("open", !isOpen(newState), isOpen(newState));
        }
    }
    
    public void addChangeListener(final PropertyChangeListener listener) {
        this.changeSupport.addPropertyChangeListener(listener);
    }
    
    public void removeChangeListener(final PropertyChangeListener listener) {
        this.changeSupport.removePropertyChangeListener(listener);
    }
    
    protected enum State
    {
        CLOSED {
            @Override
            public State oppositeState() {
                return AbstractCircuitBreaker$State$1.OPEN;
            }
        }, 
        OPEN {
            @Override
            public State oppositeState() {
                return AbstractCircuitBreaker$State$2.CLOSED;
            }
        };
        
        public abstract State oppositeState();
    }
}
