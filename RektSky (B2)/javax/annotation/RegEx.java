package javax.annotation;

import java.lang.annotation.*;
import javax.annotation.meta.*;
import java.util.regex.*;

@Documented
@Syntax("RegEx")
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface RegEx {
    When when() default When.ALWAYS;
    
    public static class Checker implements TypeQualifierValidator<RegEx>
    {
        public When forConstantValue(final RegEx annotation, final Object value) {
            if (!(value instanceof String)) {
                return When.NEVER;
            }
            try {
                Pattern.compile((String)value);
            }
            catch (PatternSyntaxException e) {
                return When.NEVER;
            }
            return When.ALWAYS;
        }
    }
}
