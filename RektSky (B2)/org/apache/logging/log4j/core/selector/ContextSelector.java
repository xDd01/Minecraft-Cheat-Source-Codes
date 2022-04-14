package org.apache.logging.log4j.core.selector;

import org.apache.logging.log4j.core.*;
import java.net.*;
import java.util.*;

public interface ContextSelector
{
    LoggerContext getContext(final String p0, final ClassLoader p1, final boolean p2);
    
    LoggerContext getContext(final String p0, final ClassLoader p1, final boolean p2, final URI p3);
    
    List<LoggerContext> getLoggerContexts();
    
    void removeContext(final LoggerContext p0);
}
