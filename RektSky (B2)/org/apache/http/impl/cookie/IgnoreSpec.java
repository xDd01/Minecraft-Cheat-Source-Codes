package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.*;
import java.util.*;
import org.apache.http.cookie.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpec extends CookieSpecBase
{
    @Override
    public int getVersion() {
        return 0;
    }
    
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        return Collections.emptyList();
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return false;
    }
    
    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        return Collections.emptyList();
    }
    
    @Override
    public Header getVersionHeader() {
        return null;
    }
}
