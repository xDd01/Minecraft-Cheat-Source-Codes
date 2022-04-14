package io.netty.util.internal.logging;

import java.util.*;
import org.apache.commons.logging.*;

public class CommonsLoggerFactory extends InternalLoggerFactory
{
    Map<String, InternalLogger> loggerMap;
    
    public CommonsLoggerFactory() {
        this.loggerMap = new HashMap<String, InternalLogger>();
    }
    
    public InternalLogger newInstance(final String name) {
        return new CommonsLogger(LogFactory.getLog(name), name);
    }
}
