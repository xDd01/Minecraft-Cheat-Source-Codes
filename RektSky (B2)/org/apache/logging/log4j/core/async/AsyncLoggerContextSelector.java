package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.core.selector.*;
import org.apache.logging.log4j.core.*;
import java.util.*;
import java.net.*;

public class AsyncLoggerContextSelector implements ContextSelector
{
    private static final AsyncLoggerContext CONTEXT;
    
    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        return AsyncLoggerContextSelector.CONTEXT;
    }
    
    @Override
    public List<LoggerContext> getLoggerContexts() {
        final List<LoggerContext> list = new ArrayList<LoggerContext>();
        list.add(AsyncLoggerContextSelector.CONTEXT);
        return Collections.unmodifiableList((List<? extends LoggerContext>)list);
    }
    
    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext, final URI configLocation) {
        return AsyncLoggerContextSelector.CONTEXT;
    }
    
    @Override
    public void removeContext(final LoggerContext context) {
    }
    
    static {
        CONTEXT = new AsyncLoggerContext("AsyncLoggerContext");
    }
}
