package javax.annotation;

import java.lang.annotation.*;
import javax.annotation.meta.*;

@Documented
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnull {
    When when() default When.ALWAYS;
    
    public static class Checker implements TypeQualifierValidator<Nonnull>
    {
        public When forConstantValue(final Nonnull qualifierqualifierArgument, final Object value) {
            if (value == null) {
                return When.NEVER;
            }
            return When.ALWAYS;
        }
    }
}
