package joptsimple.internal;

import java.util.*;
import java.text.*;

public class Messages
{
    private Messages() {
        throw new UnsupportedOperationException();
    }
    
    public static String message(final Locale locale, final String bundleName, final Class<?> type, final String key, final Object... args) {
        final ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        final String template = bundle.getString(type.getName() + '.' + key);
        final MessageFormat format = new MessageFormat(template);
        format.setLocale(locale);
        return format.format(args);
    }
}
