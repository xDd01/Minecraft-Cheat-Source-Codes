package org.apache.logging.log4j.core.impl;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.message.*;
import java.util.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.*;

public class DefaultLogEventFactory implements LogEventFactory
{
    @Override
    public LogEvent createEvent(final String loggerName, final Marker marker, final String fqcn, final Level level, final Message data, final List<Property> properties, final Throwable t) {
        return new Log4jLogEvent(loggerName, marker, fqcn, level, data, properties, t);
    }
}
