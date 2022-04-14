package org.lwjgl.util.mapped;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CacheLinePad {
    boolean before() default false;
    
    boolean after() default true;
}
