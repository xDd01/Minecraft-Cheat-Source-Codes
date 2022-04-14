package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.message.*;

public interface LoggerContext
{
    Object getExternalContext();
    
    Logger getLogger(final String p0);
    
    Logger getLogger(final String p0, final MessageFactory p1);
    
    boolean hasLogger(final String p0);
}
