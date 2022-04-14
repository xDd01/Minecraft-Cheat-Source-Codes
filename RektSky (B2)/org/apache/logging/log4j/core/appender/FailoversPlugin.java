package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.status.*;

@Plugin(name = "failovers", category = "Core")
public final class FailoversPlugin
{
    private static final Logger LOGGER;
    
    private FailoversPlugin() {
    }
    
    @PluginFactory
    public static String[] createFailovers(@PluginElement("AppenderRef") final AppenderRef... refs) {
        if (refs == null) {
            FailoversPlugin.LOGGER.error("failovers must contain an appender reference");
            return null;
        }
        final String[] arr = new String[refs.length];
        for (int i = 0; i < refs.length; ++i) {
            arr[i] = refs[i].getRef();
        }
        return arr;
    }
    
    static {
        LOGGER = StatusLogger.getLogger();
    }
}
