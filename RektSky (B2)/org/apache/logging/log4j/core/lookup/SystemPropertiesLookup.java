package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.*;

@Plugin(name = "sys", category = "Lookup")
public class SystemPropertiesLookup implements StrLookup
{
    @Override
    public String lookup(final String key) {
        try {
            return System.getProperty(key);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public String lookup(final LogEvent event, final String key) {
        try {
            return System.getProperty(key);
        }
        catch (Exception ex) {
            return null;
        }
    }
}
