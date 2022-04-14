package org.apache.logging.log4j.message;

public interface LoggerNameAwareMessage
{
    void setLoggerName(final String p0);
    
    String getLoggerName();
}
