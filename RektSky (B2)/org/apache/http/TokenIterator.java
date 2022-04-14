package org.apache.http;

import java.util.*;

public interface TokenIterator extends Iterator<Object>
{
    boolean hasNext();
    
    String nextToken();
}
