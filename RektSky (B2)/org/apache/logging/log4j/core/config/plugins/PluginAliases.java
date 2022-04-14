package org.apache.logging.log4j.core.config.plugins;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.TYPE })
public @interface PluginAliases {
    String[] value();
}
