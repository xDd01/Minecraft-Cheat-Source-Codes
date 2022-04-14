package org.apache.commons.lang3.time;

import java.util.*;
import java.text.*;

public interface DatePrinter
{
    String format(final long p0);
    
    String format(final Date p0);
    
    String format(final Calendar p0);
    
    @Deprecated
    StringBuffer format(final long p0, final StringBuffer p1);
    
    @Deprecated
    StringBuffer format(final Date p0, final StringBuffer p1);
    
    @Deprecated
    StringBuffer format(final Calendar p0, final StringBuffer p1);
    
     <B extends Appendable> B format(final long p0, final B p1);
    
     <B extends Appendable> B format(final Date p0, final B p1);
    
     <B extends Appendable> B format(final Calendar p0, final B p1);
    
    String getPattern();
    
    TimeZone getTimeZone();
    
    Locale getLocale();
    
    StringBuffer format(final Object p0, final StringBuffer p1, final FieldPosition p2);
}
