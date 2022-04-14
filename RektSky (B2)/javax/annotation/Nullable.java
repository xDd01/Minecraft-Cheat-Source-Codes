package javax.annotation;

import java.lang.annotation.*;
import javax.annotation.meta.*;

@Documented
@Nonnull(when = When.UNKNOWN)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface Nullable {
}
