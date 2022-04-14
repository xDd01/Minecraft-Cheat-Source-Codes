package org.apache.http.cookie;

import java.io.*;
import java.util.*;
import org.apache.http.annotation.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class CookiePathComparator implements Serializable, Comparator<Cookie>
{
    public static final CookiePathComparator INSTANCE;
    private static final long serialVersionUID = 7523645369616405818L;
    
    private String normalizePath(final Cookie cookie) {
        String path = cookie.getPath();
        if (path == null) {
            path = "/";
        }
        if (!path.endsWith("/")) {
            path += '/';
        }
        return path;
    }
    
    @Override
    public int compare(final Cookie c1, final Cookie c2) {
        final String path1 = this.normalizePath(c1);
        final String path2 = this.normalizePath(c2);
        if (path1.equals(path2)) {
            return 0;
        }
        if (path1.startsWith(path2)) {
            return -1;
        }
        if (path2.startsWith(path1)) {
            return 1;
        }
        return 0;
    }
    
    static {
        INSTANCE = new CookiePathComparator();
    }
}
