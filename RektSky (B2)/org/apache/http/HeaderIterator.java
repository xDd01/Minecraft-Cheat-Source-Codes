package org.apache.http;

import java.util.*;

public interface HeaderIterator extends Iterator<Object>
{
    boolean hasNext();
    
    Header nextHeader();
}
