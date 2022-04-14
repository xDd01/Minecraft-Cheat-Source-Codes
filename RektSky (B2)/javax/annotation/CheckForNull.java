package javax.annotation;

import java.lang.annotation.*;
import javax.annotation.meta.*;

@Documented
@Nonnull(when = When.MAYBE)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface CheckForNull {
}
