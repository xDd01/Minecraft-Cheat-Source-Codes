package org.apache.logging.log4j.core.config.plugins;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Plugin {
    public static final String EMPTY = "";
    
    String name();
    
    String category();
    
    String elementType() default "";
    
    boolean printObject() default false;
    
    boolean deferChildren() default false;
}
