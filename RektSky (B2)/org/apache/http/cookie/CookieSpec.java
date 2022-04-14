package org.apache.http.cookie;

import org.apache.http.annotation.*;
import org.apache.http.*;
import java.util.*;

public interface CookieSpec
{
    @Obsolete
    int getVersion();
    
    List<Cookie> parse(final Header p0, final CookieOrigin p1) throws MalformedCookieException;
    
    void validate(final Cookie p0, final CookieOrigin p1) throws MalformedCookieException;
    
    boolean match(final Cookie p0, final CookieOrigin p1);
    
    List<Header> formatCookies(final List<Cookie> p0);
    
    @Obsolete
    Header getVersionHeader();
}
