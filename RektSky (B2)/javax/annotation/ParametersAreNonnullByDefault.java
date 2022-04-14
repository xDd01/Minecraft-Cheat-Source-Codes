package javax.annotation;

import javax.annotation.meta.*;
import java.lang.annotation.*;

@Documented
@Nonnull
@TypeQualifierDefault({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersAreNonnullByDefault {
}
