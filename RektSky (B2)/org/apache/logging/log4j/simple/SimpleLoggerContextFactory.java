package org.apache.logging.log4j.simple;

import org.apache.logging.log4j.spi.*;
import java.net.*;

public class SimpleLoggerContextFactory implements LoggerContextFactory
{
    private static LoggerContext context;
    
    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        return SimpleLoggerContextFactory.context;
    }
    
    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext, final URI configLocation) {
        return SimpleLoggerContextFactory.context;
    }
    
    @Override
    public void removeContext(final LoggerContext context) {
    }
    
    static {
        SimpleLoggerContextFactory.context = new SimpleLoggerContext();
    }
}
