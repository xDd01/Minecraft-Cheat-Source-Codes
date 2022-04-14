package javax.annotation;

import java.lang.annotation.*;
import javax.annotation.meta.*;
import java.util.regex.*;

@Documented
@TypeQualifier(applicableTo = String.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchesPattern {
    @RegEx
    String value();
    
    int flags() default 0;
    
    public static class Checker implements TypeQualifierValidator<MatchesPattern>
    {
        public When forConstantValue(final MatchesPattern annotation, final Object value) {
            final Pattern p = Pattern.compile(annotation.value(), annotation.flags());
            if (p.matcher((CharSequence)value).matches()) {
                return When.ALWAYS;
            }
            return When.NEVER;
        }
    }
}
