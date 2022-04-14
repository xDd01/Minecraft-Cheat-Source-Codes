package javax.annotation.meta;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeQualifier {
    Class<?> applicableTo() default Object.class;
}
