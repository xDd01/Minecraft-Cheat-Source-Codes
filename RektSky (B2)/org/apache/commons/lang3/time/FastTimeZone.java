package org.apache.commons.lang3.time;

import java.util.*;
import java.util.regex.*;

public class FastTimeZone
{
    private static final Pattern GMT_PATTERN;
    private static final TimeZone GREENWICH;
    
    public static TimeZone getGmtTimeZone() {
        return FastTimeZone.GREENWICH;
    }
    
    public static TimeZone getGmtTimeZone(final String pattern) {
        if ("Z".equals(pattern) || "UTC".equals(pattern)) {
            return FastTimeZone.GREENWICH;
        }
        final Matcher m = FastTimeZone.GMT_PATTERN.matcher(pattern);
        if (!m.matches()) {
            return null;
        }
        final int hours = parseInt(m.group(2));
        final int minutes = parseInt(m.group(4));
        if (hours == 0 && minutes == 0) {
            return FastTimeZone.GREENWICH;
        }
        return new GmtTimeZone(parseSign(m.group(1)), hours, minutes);
    }
    
    public static TimeZone getTimeZone(final String id) {
        final TimeZone tz = getGmtTimeZone(id);
        if (tz != null) {
            return tz;
        }
        return TimeZone.getTimeZone(id);
    }
    
    private static int parseInt(final String group) {
        return (group != null) ? Integer.parseInt(group) : 0;
    }
    
    private static boolean parseSign(final String group) {
        return group != null && group.charAt(0) == '-';
    }
    
    private FastTimeZone() {
    }
    
    static {
        GMT_PATTERN = Pattern.compile("^(?:(?i)GMT)?([+-])?(\\d\\d?)?(:?(\\d\\d?))?$");
        GREENWICH = new GmtTimeZone(false, 0, 0);
    }
}
