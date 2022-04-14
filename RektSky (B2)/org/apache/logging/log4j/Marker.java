package org.apache.logging.log4j;

import java.io.*;

public interface Marker extends Serializable
{
    String getName();
    
    Marker getParent();
    
    boolean isInstanceOf(final Marker p0);
    
    boolean isInstanceOf(final String p0);
}
