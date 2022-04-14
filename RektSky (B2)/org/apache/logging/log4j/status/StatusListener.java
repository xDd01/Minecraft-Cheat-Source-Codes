package org.apache.logging.log4j.status;

import org.apache.logging.log4j.*;

public interface StatusListener
{
    void log(final StatusData p0);
    
    Level getStatusLevel();
}
