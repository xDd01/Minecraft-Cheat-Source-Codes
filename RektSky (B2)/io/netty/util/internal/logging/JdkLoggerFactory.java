package io.netty.util.internal.logging;

import java.util.logging.*;

public class JdkLoggerFactory extends InternalLoggerFactory
{
    public InternalLogger newInstance(final String name) {
        return new JdkLogger(Logger.getLogger(name));
    }
}
