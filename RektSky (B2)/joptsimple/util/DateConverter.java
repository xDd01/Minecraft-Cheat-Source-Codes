package joptsimple.util;

import java.text.*;
import joptsimple.*;
import java.util.*;
import joptsimple.internal.*;

public class DateConverter implements ValueConverter<Date>
{
    private final DateFormat formatter;
    
    public DateConverter(final DateFormat formatter) {
        if (formatter == null) {
            throw new NullPointerException("illegal null formatter");
        }
        this.formatter = formatter;
    }
    
    public static DateConverter datePattern(final String pattern) {
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false);
        return new DateConverter(formatter);
    }
    
    public Date convert(final String value) {
        final ParsePosition position = new ParsePosition(0);
        final Date date = this.formatter.parse(value, position);
        if (position.getIndex() != value.length()) {
            throw new ValueConversionException(this.message(value));
        }
        return date;
    }
    
    public Class<Date> valueType() {
        return Date.class;
    }
    
    public String valuePattern() {
        return (this.formatter instanceof SimpleDateFormat) ? ((SimpleDateFormat)this.formatter).toPattern() : "";
    }
    
    private String message(final String value) {
        String key;
        Object[] arguments;
        if (this.formatter instanceof SimpleDateFormat) {
            key = "with.pattern.message";
            arguments = new Object[] { value, ((SimpleDateFormat)this.formatter).toPattern() };
        }
        else {
            key = "without.pattern.message";
            arguments = new Object[] { value };
        }
        return Messages.message(Locale.getDefault(), "joptsimple.ExceptionMessages", DateConverter.class, key, arguments);
    }
}
