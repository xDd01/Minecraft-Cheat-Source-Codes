package org.apache.logging.log4j.core.config.plugins;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface PluginElement {
    String value();
}
