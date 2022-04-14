package org.apache.logging.log4j.core.appender.rewrite;

import org.apache.logging.log4j.core.*;

public interface RewritePolicy
{
    LogEvent rewrite(final LogEvent p0);
}
