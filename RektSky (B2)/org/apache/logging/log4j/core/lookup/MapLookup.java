package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.config.plugins.*;
import java.util.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.message.*;

@Plugin(name = "map", category = "Lookup")
public class MapLookup implements StrLookup
{
    private final Map<String, String> map;
    
    public MapLookup(final Map<String, String> map) {
        this.map = map;
    }
    
    public MapLookup() {
        this.map = null;
    }
    
    @Override
    public String lookup(final String key) {
        if (this.map == null) {
            return null;
        }
        final String obj = this.map.get(key);
        if (obj == null) {
            return null;
        }
        return obj;
    }
    
    @Override
    public String lookup(final LogEvent event, final String key) {
        if (this.map == null && !(event.getMessage() instanceof MapMessage)) {
            return null;
        }
        if (this.map != null && this.map.containsKey(key)) {
            final String obj = this.map.get(key);
            if (obj != null) {
                return obj;
            }
        }
        if (event.getMessage() instanceof MapMessage) {
            return ((MapMessage)event.getMessage()).get(key);
        }
        return null;
    }
}
