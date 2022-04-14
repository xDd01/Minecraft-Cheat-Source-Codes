package org.apache.logging.log4j.core.helpers;

public class Booleans
{
    public static boolean parseBoolean(final String s, final boolean defaultValue) {
        return "true".equalsIgnoreCase(s) || (defaultValue && !"false".equalsIgnoreCase(s));
    }
}
