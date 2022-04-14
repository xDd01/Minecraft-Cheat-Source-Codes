package org.apache.logging.log4j.core.impl;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.message.*;
import java.util.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.*;

public interface LogEventFactory
{
    LogEvent createEvent(final String p0, final Marker p1, final String p2, final Level p3, final Message p4, final List<Property> p5, final Throwable p6);
}
