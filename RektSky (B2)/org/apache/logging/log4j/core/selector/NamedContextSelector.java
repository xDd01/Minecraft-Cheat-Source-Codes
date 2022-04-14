package org.apache.logging.log4j.core.selector;

import java.net.*;
import org.apache.logging.log4j.core.*;

public interface NamedContextSelector extends ContextSelector
{
    LoggerContext locateContext(final String p0, final Object p1, final URI p2);
    
    LoggerContext removeContext(final String p0);
}
