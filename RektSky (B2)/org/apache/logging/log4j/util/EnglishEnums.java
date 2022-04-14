package org.apache.logging.log4j.util;

import java.util.*;

public final class EnglishEnums
{
    private EnglishEnums() {
    }
    
    public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {
        return valueOf(enumType, name, (T)null);
    }
    
    public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name, final T defaultValue) {
        return (name == null) ? defaultValue : Enum.valueOf(enumType, name.toUpperCase(Locale.ENGLISH));
    }
}
