package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import org.apache.http.message.*;
import org.apache.http.cookie.*;
import java.util.*;
import java.util.regex.*;
import java.util.concurrent.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxExpiresHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    static final TimeZone UTC;
    private static final BitSet DELIMS;
    private static final Map<String, Integer> MONTHS;
    private static final Pattern TIME_PATTERN;
    private static final Pattern DAY_OF_MONTH_PATTERN;
    private static final Pattern MONTH_PATTERN;
    private static final Pattern YEAR_PATTERN;
    
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        final ParserCursor cursor = new ParserCursor(0, value.length());
        final StringBuilder content = new StringBuilder();
        int second = 0;
        int minute = 0;
        int hour = 0;
        int day = 0;
        int month = 0;
        int year = 0;
        boolean foundTime = false;
        boolean foundDayOfMonth = false;
        boolean foundMonth = false;
        boolean foundYear = false;
        try {
            while (!cursor.atEnd()) {
                this.skipDelims(value, cursor);
                content.setLength(0);
                this.copyContent(value, cursor, content);
                if (content.length() == 0) {
                    break;
                }
                if (!foundTime) {
                    final Matcher matcher = LaxExpiresHandler.TIME_PATTERN.matcher(content);
                    if (matcher.matches()) {
                        foundTime = true;
                        hour = Integer.parseInt(matcher.group(1));
                        minute = Integer.parseInt(matcher.group(2));
                        second = Integer.parseInt(matcher.group(3));
                        continue;
                    }
                }
                if (!foundDayOfMonth) {
                    final Matcher matcher = LaxExpiresHandler.DAY_OF_MONTH_PATTERN.matcher(content);
                    if (matcher.matches()) {
                        foundDayOfMonth = true;
                        day = Integer.parseInt(matcher.group(1));
                        continue;
                    }
                }
                if (!foundMonth) {
                    final Matcher matcher = LaxExpiresHandler.MONTH_PATTERN.matcher(content);
                    if (matcher.matches()) {
                        foundMonth = true;
                        month = LaxExpiresHandler.MONTHS.get(matcher.group(1).toLowerCase(Locale.ROOT));
                        continue;
                    }
                }
                if (foundYear) {
                    continue;
                }
                final Matcher matcher = LaxExpiresHandler.YEAR_PATTERN.matcher(content);
                if (!matcher.matches()) {
                    continue;
                }
                foundYear = true;
                year = Integer.parseInt(matcher.group(1));
            }
        }
        catch (NumberFormatException ignore) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
        }
        if (!foundTime || !foundDayOfMonth || !foundMonth || !foundYear) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
        }
        if (year >= 70 && year <= 99) {
            year += 1900;
        }
        if (year >= 0 && year <= 69) {
            year += 2000;
        }
        if (day < 1 || day > 31 || year < 1601 || hour > 23 || minute > 59 || second > 59) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
        }
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(LaxExpiresHandler.UTC);
        c.setTimeInMillis(0L);
        c.set(13, second);
        c.set(12, minute);
        c.set(11, hour);
        c.set(5, day);
        c.set(2, month);
        c.set(1, year);
        cookie.setExpiryDate(c.getTime());
    }
    
    private void skipDelims(final CharSequence buf, final ParserCursor cursor) {
        int pos = cursor.getPos();
        final int indexFrom = cursor.getPos();
        for (int indexTo = cursor.getUpperBound(), i = indexFrom; i < indexTo; ++i) {
            final char current = buf.charAt(i);
            if (!LaxExpiresHandler.DELIMS.get(current)) {
                break;
            }
            ++pos;
        }
        cursor.updatePos(pos);
    }
    
    private void copyContent(final CharSequence buf, final ParserCursor cursor, final StringBuilder dst) {
        int pos = cursor.getPos();
        final int indexFrom = cursor.getPos();
        for (int indexTo = cursor.getUpperBound(), i = indexFrom; i < indexTo; ++i) {
            final char current = buf.charAt(i);
            if (LaxExpiresHandler.DELIMS.get(current)) {
                break;
            }
            ++pos;
            dst.append(current);
        }
        cursor.updatePos(pos);
    }
    
    @Override
    public String getAttributeName() {
        return "expires";
    }
    
    static {
        UTC = TimeZone.getTimeZone("UTC");
        final BitSet bitSet = new BitSet();
        bitSet.set(9);
        for (int b = 32; b <= 47; ++b) {
            bitSet.set(b);
        }
        for (int b = 59; b <= 64; ++b) {
            bitSet.set(b);
        }
        for (int b = 91; b <= 96; ++b) {
            bitSet.set(b);
        }
        for (int b = 123; b <= 126; ++b) {
            bitSet.set(b);
        }
        DELIMS = bitSet;
        final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>(12);
        map.put("jan", 0);
        map.put("feb", 1);
        map.put("mar", 2);
        map.put("apr", 3);
        map.put("may", 4);
        map.put("jun", 5);
        map.put("jul", 6);
        map.put("aug", 7);
        map.put("sep", 8);
        map.put("oct", 9);
        map.put("nov", 10);
        map.put("dec", 11);
        MONTHS = map;
        TIME_PATTERN = Pattern.compile("^([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2})([^0-9].*)?$");
        DAY_OF_MONTH_PATTERN = Pattern.compile("^([0-9]{1,2})([^0-9].*)?$");
        MONTH_PATTERN = Pattern.compile("^(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(.*)?$", 2);
        YEAR_PATTERN = Pattern.compile("^([0-9]{2,4})([^0-9].*)?$");
    }
}
