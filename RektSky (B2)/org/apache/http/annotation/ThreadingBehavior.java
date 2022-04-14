package org.apache.http.annotation;

public enum ThreadingBehavior
{
    IMMUTABLE, 
    IMMUTABLE_CONDITIONAL, 
    SAFE, 
    SAFE_CONDITIONAL, 
    UNSAFE;
}
