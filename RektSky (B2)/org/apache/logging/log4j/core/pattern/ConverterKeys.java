package org.apache.logging.log4j.core.pattern;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ConverterKeys {
    String[] value();
}
