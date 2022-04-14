package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.*;

@Plugin(name = "env", category = "Lookup")
public class EnvironmentLookup implements StrLookup
{
    @Override
    public String lookup(final String key) {
        return System.getenv(key);
    }
    
    @Override
    public String lookup(final LogEvent event, final String key) {
        return System.getenv(key);
    }
}
