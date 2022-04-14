package org.apache.http.cookie;

import org.apache.http.annotation.*;
import org.apache.http.impl.cookie.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class CookiePriorityComparator implements Comparator<Cookie>
{
    public static final CookiePriorityComparator INSTANCE;
    
    private int getPathLength(final Cookie cookie) {
        final String path = cookie.getPath();
        return (path != null) ? path.length() : 1;
    }
    
    @Override
    public int compare(final Cookie c1, final Cookie c2) {
        final int l1 = this.getPathLength(c1);
        final int l2 = this.getPathLength(c2);
        final int result = l2 - l1;
        if (result == 0 && c1 instanceof BasicClientCookie && c2 instanceof BasicClientCookie) {
            final Date d1 = ((BasicClientCookie)c1).getCreationDate();
            final Date d2 = ((BasicClientCookie)c2).getCreationDate();
            if (d1 != null && d2 != null) {
                return (int)(d1.getTime() - d2.getTime());
            }
        }
        return result;
    }
    
    static {
        INSTANCE = new CookiePriorityComparator();
    }
}
