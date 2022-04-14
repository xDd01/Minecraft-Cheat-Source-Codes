package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.message.*;

@Plugin(name = "sd", category = "Lookup")
public class StructuredDataLookup implements StrLookup
{
    @Override
    public String lookup(final String key) {
        return null;
    }
    
    @Override
    public String lookup(final LogEvent event, final String key) {
        if (event == null || !(event.getMessage() instanceof StructuredDataMessage)) {
            return null;
        }
        final StructuredDataMessage msg = (StructuredDataMessage)event.getMessage();
        if (key.equalsIgnoreCase("id")) {
            return msg.getId().getName();
        }
        if (key.equalsIgnoreCase("type")) {
            return msg.getType();
        }
        return msg.get(key);
    }
}
