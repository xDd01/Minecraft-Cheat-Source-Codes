package org.apache.commons.lang3.time;

import java.util.*;

public class DateFormatUtils
{
    private static final TimeZone UTC_TIME_ZONE;
    public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_DATETIME_FORMAT;
    public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT;
    public static final FastDateFormat ISO_8601_EXTENDED_DATE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_DATE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_DATE_TIME_ZONE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_TIME_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_TIME_TIME_ZONE_FORMAT;
    public static final FastDateFormat ISO_8601_EXTENDED_TIME_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_TIME_NO_T_FORMAT;
    public static final FastDateFormat ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT;
    @Deprecated
    public static final FastDateFormat ISO_TIME_NO_T_TIME_ZONE_FORMAT;
    public static final FastDateFormat SMTP_DATETIME_FORMAT;
    
    public static String formatUTC(final long millis, final String pattern) {
        return format(new Date(millis), pattern, DateFormatUtils.UTC_TIME_ZONE, null);
    }
    
    public static String formatUTC(final Date date, final String pattern) {
        return format(date, pattern, DateFormatUtils.UTC_TIME_ZONE, null);
    }
    
    public static String formatUTC(final long millis, final String pattern, final Locale locale) {
        return format(new Date(millis), pattern, DateFormatUtils.UTC_TIME_ZONE, locale);
    }
    
    public static String formatUTC(final Date date, final String pattern, final Locale locale) {
        return format(date, pattern, DateFormatUtils.UTC_TIME_ZONE, locale);
    }
    
    public static String format(final long millis, final String pattern) {
        return format(new Date(millis), pattern, null, null);
    }
    
    public static String format(final Date date, final String pattern) {
        return format(date, pattern, null, null);
    }
    
    public static String format(final Calendar calendar, final String pattern) {
        return format(calendar, pattern, null, null);
    }
    
    public static String format(final long millis, final String pattern, final TimeZone timeZone) {
        return format(new Date(millis), pattern, timeZone, null);
    }
    
    public static String format(final Date date, final String pattern, final TimeZone timeZone) {
        return format(date, pattern, timeZone, null);
    }
    
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone) {
        return format(calendar, pattern, timeZone, null);
    }
    
    public static String format(final long millis, final String pattern, final Locale locale) {
        return format(new Date(millis), pattern, null, locale);
    }
    
    public static String format(final Date date, final String pattern, final Locale locale) {
        return format(date, pattern, null, locale);
    }
    
    public static String format(final Calendar calendar, final String pattern, final Locale locale) {
        return format(calendar, pattern, null, locale);
    }
    
    public static String format(final long millis, final String pattern, final TimeZone timeZone, final Locale locale) {
        return format(new Date(millis), pattern, timeZone, locale);
    }
    
    public static String format(final Date date, final String pattern, final TimeZone timeZone, final Locale locale) {
        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(date);
    }
    
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone, final Locale locale) {
        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(calendar);
    }
    
    static {
        UTC_TIME_ZONE = FastTimeZone.getGmtTimeZone();
        ISO_8601_EXTENDED_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
        ISO_DATETIME_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT;
        ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");
        ISO_DATETIME_TIME_ZONE_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT;
        ISO_8601_EXTENDED_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
        ISO_DATE_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;
        ISO_DATE_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-ddZZ");
        ISO_TIME_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ss");
        ISO_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ssZZ");
        ISO_8601_EXTENDED_TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");
        ISO_TIME_NO_T_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT;
        ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("HH:mm:ssZZ");
        ISO_TIME_NO_T_TIME_ZONE_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT;
        SMTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    }
}
