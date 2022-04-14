package org.apache.logging.log4j.core.config.plugins;

import org.apache.logging.log4j.core.*;
import java.util.concurrent.*;

@Plugin(name = "appenders", category = "Core")
public final class AppendersPlugin
{
    private AppendersPlugin() {
    }
    
    @PluginFactory
    public static ConcurrentMap<String, Appender> createAppenders(@PluginElement("Appenders") final Appender[] appenders) {
        final ConcurrentMap<String, Appender> map = new ConcurrentHashMap<String, Appender>();
        for (final Appender appender : appenders) {
            map.put(appender.getName(), appender);
        }
        return map;
    }
}
