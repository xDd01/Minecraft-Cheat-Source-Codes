package org.apache.http.client;

import org.apache.http.cookie.*;
import java.util.*;

public interface CookieStore
{
    void addCookie(final Cookie p0);
    
    List<Cookie> getCookies();
    
    boolean clearExpired(final Date p0);
    
    void clear();
}
