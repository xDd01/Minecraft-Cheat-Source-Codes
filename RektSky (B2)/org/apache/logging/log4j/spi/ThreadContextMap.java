package org.apache.logging.log4j.spi;

import java.util.*;

public interface ThreadContextMap
{
    void put(final String p0, final String p1);
    
    String get(final String p0);
    
    void remove(final String p0);
    
    void clear();
    
    boolean containsKey(final String p0);
    
    Map<String, String> getCopy();
    
    Map<String, String> getImmutableMapOrNull();
    
    boolean isEmpty();
}
