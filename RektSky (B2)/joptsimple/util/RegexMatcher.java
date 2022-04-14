package joptsimple.util;

import java.util.regex.*;
import java.util.*;
import joptsimple.internal.*;
import joptsimple.*;

public class RegexMatcher implements ValueConverter<String>
{
    private final Pattern pattern;
    
    public RegexMatcher(final String pattern, final int flags) {
        this.pattern = Pattern.compile(pattern, flags);
    }
    
    public static ValueConverter<String> regex(final String pattern) {
        return new RegexMatcher(pattern, 0);
    }
    
    public String convert(final String value) {
        if (!this.pattern.matcher(value).matches()) {
            this.raiseValueConversionFailure(value);
        }
        return value;
    }
    
    public Class<String> valueType() {
        return String.class;
    }
    
    public String valuePattern() {
        return this.pattern.pattern();
    }
    
    private void raiseValueConversionFailure(final String value) {
        final String message = Messages.message(Locale.getDefault(), "joptsimple.ExceptionMessages", RegexMatcher.class, "message", value, this.pattern.pattern());
        throw new ValueConversionException(message);
    }
}
