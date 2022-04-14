package org.apache.logging.log4j.core.config.plugins;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface PluginFactory {
}
