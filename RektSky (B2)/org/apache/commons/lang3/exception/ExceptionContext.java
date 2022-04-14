package org.apache.commons.lang3.exception;

import java.util.*;
import org.apache.commons.lang3.tuple.*;

public interface ExceptionContext
{
    ExceptionContext addContextValue(final String p0, final Object p1);
    
    ExceptionContext setContextValue(final String p0, final Object p1);
    
    List<Object> getContextValues(final String p0);
    
    Object getFirstContextValue(final String p0);
    
    Set<String> getContextLabels();
    
    List<Pair<String, Object>> getContextEntries();
    
    String getFormattedExceptionMessage(final String p0);
}
