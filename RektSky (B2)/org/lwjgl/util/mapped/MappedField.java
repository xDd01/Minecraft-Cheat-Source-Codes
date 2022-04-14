package org.lwjgl.util.mapped;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface MappedField {
    long byteOffset() default -1L;
    
    long byteLength() default -1L;
}
