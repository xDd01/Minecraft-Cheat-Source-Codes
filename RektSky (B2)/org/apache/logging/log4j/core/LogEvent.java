package org.apache.logging.log4j.core;

import java.io.*;
import org.apache.logging.log4j.message.*;
import java.util.*;
import org.apache.logging.log4j.*;

public interface LogEvent extends Serializable
{
    Level getLevel();
    
    String getLoggerName();
    
    StackTraceElement getSource();
    
    Message getMessage();
    
    Marker getMarker();
    
    String getThreadName();
    
    long getMillis();
    
    Throwable getThrown();
    
    Map<String, String> getContextMap();
    
    ThreadContext.ContextStack getContextStack();
    
    String getFQCN();
    
    boolean isIncludeLocation();
    
    void setIncludeLocation(final boolean p0);
    
    boolean isEndOfBatch();
    
    void setEndOfBatch(final boolean p0);
}
