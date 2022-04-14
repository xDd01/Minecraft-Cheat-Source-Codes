package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.status.*;

public abstract class AbstractRolloverStrategy implements RolloverStrategy
{
    protected static final Logger LOGGER;
    
    static {
        LOGGER = StatusLogger.getLogger();
    }
}
