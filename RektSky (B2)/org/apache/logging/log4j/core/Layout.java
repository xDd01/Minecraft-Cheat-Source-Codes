package org.apache.logging.log4j.core;

import java.io.*;
import java.util.*;

public interface Layout<T extends Serializable>
{
    byte[] getFooter();
    
    byte[] getHeader();
    
    byte[] toByteArray(final LogEvent p0);
    
    T toSerializable(final LogEvent p0);
    
    String getContentType();
    
    Map<String, String> getContentFormat();
}
