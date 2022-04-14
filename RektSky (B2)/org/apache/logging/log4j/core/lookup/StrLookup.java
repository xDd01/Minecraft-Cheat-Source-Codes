package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.*;

public interface StrLookup
{
    String lookup(final String p0);
    
    String lookup(final LogEvent p0, final String p1);
}
