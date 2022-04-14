package javax.annotation;

import java.lang.annotation.*;
import javax.annotation.meta.*;

@Documented
@TypeQualifier(applicableTo = String.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Syntax {
    String value();
    
    When when() default When.ALWAYS;
}
