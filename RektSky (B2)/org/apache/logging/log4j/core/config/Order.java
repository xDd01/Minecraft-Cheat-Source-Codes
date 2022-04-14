package org.apache.logging.log4j.core.config;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Order {
    int value();
}
