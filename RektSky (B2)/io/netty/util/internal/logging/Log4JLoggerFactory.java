package io.netty.util.internal.logging;

import org.apache.log4j.*;

public class Log4JLoggerFactory extends InternalLoggerFactory
{
    public InternalLogger newInstance(final String name) {
        return new Log4JLogger(Logger.getLogger(name));
    }
}
