package org.apache.logging.log4j.core.config.plugins;

import org.apache.logging.log4j.core.config.*;
import java.util.concurrent.*;

@Plugin(name = "loggers", category = "Core")
public final class LoggersPlugin
{
    private LoggersPlugin() {
    }
    
    @PluginFactory
    public static Loggers createLoggers(@PluginElement("Loggers") final LoggerConfig[] loggers) {
        final ConcurrentMap<String, LoggerConfig> loggerMap = new ConcurrentHashMap<String, LoggerConfig>();
        LoggerConfig root = null;
        for (final LoggerConfig logger : loggers) {
            if (logger != null) {
                if (logger.getName().isEmpty()) {
                    root = logger;
                }
                loggerMap.put(logger.getName(), logger);
            }
        }
        return new Loggers(loggerMap, root);
    }
}
