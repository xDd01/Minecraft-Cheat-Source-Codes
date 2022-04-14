package org.apache.http.annotation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface Contract {
    ThreadingBehavior threading() default ThreadingBehavior.UNSAFE;
}
