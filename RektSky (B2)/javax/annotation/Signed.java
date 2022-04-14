package javax.annotation;

import java.lang.annotation.*;
import javax.annotation.meta.*;

@Documented
@Nonnegative(when = When.UNKNOWN)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface Signed {
}
