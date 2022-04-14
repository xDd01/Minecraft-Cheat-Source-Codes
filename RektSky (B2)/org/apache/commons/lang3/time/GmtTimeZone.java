package org.apache.commons.lang3.time;

import java.util.*;

class GmtTimeZone extends TimeZone
{
    private static final int MILLISECONDS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    static final long serialVersionUID = 1L;
    private final int offset;
    private final String zoneId;
    
    GmtTimeZone(final boolean negate, final int hours, final int minutes) {
        if (hours >= 24) {
            throw new IllegalArgumentException(hours + " hours out of range");
        }
        if (minutes >= 60) {
            throw new IllegalArgumentException(minutes + " minutes out of range");
        }
        final int milliseconds = (minutes + hours * 60) * 60000;
        this.offset = (negate ? (-milliseconds) : milliseconds);
        this.zoneId = twoDigits(twoDigits(new StringBuilder(9).append("GMT").append(negate ? '-' : '+'), hours).append(':'), minutes).toString();
    }
    
    private static StringBuilder twoDigits(final StringBuilder sb, final int n) {
        return sb.append((char)(48 + n / 10)).append((char)(48 + n % 10));
    }
    
    @Override
    public int getOffset(final int era, final int year, final int month, final int day, final int dayOfWeek, final int milliseconds) {
        return this.offset;
    }
    
    @Override
    public void setRawOffset(final int offsetMillis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getRawOffset() {
        return this.offset;
    }
    
    @Override
    public String getID() {
        return this.zoneId;
    }
    
    @Override
    public boolean useDaylightTime() {
        return false;
    }
    
    @Override
    public boolean inDaylightTime(final Date date) {
        return false;
    }
    
    @Override
    public String toString() {
        return "[GmtTimeZone id=\"" + this.zoneId + "\",offset=" + this.offset + ']';
    }
    
    @Override
    public int hashCode() {
        return this.offset;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof GmtTimeZone && this.zoneId == ((GmtTimeZone)other).zoneId;
    }
}
