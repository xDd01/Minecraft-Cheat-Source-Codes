/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name="failovers", category="Core")
public final class FailoversPlugin {
    private static final Logger LOGGER = StatusLogger.getLogger();

    private FailoversPlugin() {
    }

    @PluginFactory
    public static String[] createFailovers(AppenderRef ... refs) {
        if (refs == null) {
            LOGGER.error("failovers must contain an appender reference");
            return null;
        }
        String[] arr2 = new String[refs.length];
        for (int i2 = 0; i2 < refs.length; ++i2) {
            arr2[i2] = refs[i2].getRef();
        }
        return arr2;
    }
}

