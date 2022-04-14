package org.apache.commons.lang3.builder;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ToStringExclude {
}
