package javax.annotation.meta;

import java.lang.annotation.*;
import javax.annotation.*;

public interface TypeQualifierValidator<A extends Annotation>
{
    @Nonnull
    When forConstantValue(@Nonnull final A p0, final Object p1);
}
