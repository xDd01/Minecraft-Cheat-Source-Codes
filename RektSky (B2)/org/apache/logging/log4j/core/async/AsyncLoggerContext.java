package org.apache.logging.log4j.core.async;

import java.net.*;
import org.apache.logging.log4j.message.*;
import org.apache.logging.log4j.core.*;

public class AsyncLoggerContext extends LoggerContext
{
    public AsyncLoggerContext(final String name) {
        super(name);
    }
    
    public AsyncLoggerContext(final String name, final Object externalContext) {
        super(name, externalContext);
    }
    
    public AsyncLoggerContext(final String name, final Object externalContext, final URI configLocn) {
        super(name, externalContext, configLocn);
    }
    
    public AsyncLoggerContext(final String name, final Object externalContext, final String configLocn) {
        super(name, externalContext, configLocn);
    }
    
    @Override
    protected Logger newInstance(final LoggerContext ctx, final String name, final MessageFactory messageFactory) {
        return new AsyncLogger(ctx, name, messageFactory);
    }
    
    @Override
    public void stop() {
        AsyncLogger.stop();
        super.stop();
    }
}
