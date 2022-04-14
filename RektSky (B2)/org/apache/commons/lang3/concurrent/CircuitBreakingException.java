package org.apache.commons.lang3.concurrent;

public class CircuitBreakingException extends RuntimeException
{
    private static final long serialVersionUID = 1408176654686913340L;
    
    public CircuitBreakingException() {
    }
    
    public CircuitBreakingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CircuitBreakingException(final String message) {
        super(message);
    }
    
    public CircuitBreakingException(final Throwable cause) {
        super(cause);
    }
}
