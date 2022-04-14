package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.*;
import org.apache.http.cookie.*;
import org.apache.http.util.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.SAFE)
public abstract class CookieSpecBase extends AbstractCookieSpec
{
    public CookieSpecBase() {
    }
    
    protected CookieSpecBase(final HashMap<String, CookieAttributeHandler> map) {
        super(map);
    }
    
    protected CookieSpecBase(final CommonCookieAttributeHandler... handlers) {
        super(handlers);
    }
    
    protected static String getDefaultPath(final CookieOrigin origin) {
        String defaultPath = origin.getPath();
        int lastSlashIndex = defaultPath.lastIndexOf(47);
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                lastSlashIndex = 1;
            }
            defaultPath = defaultPath.substring(0, lastSlashIndex);
        }
        return defaultPath;
    }
    
    protected static String getDefaultDomain(final CookieOrigin origin) {
        return origin.getHost();
    }
    
    protected List<Cookie> parse(final HeaderElement[] elems, final CookieOrigin origin) throws MalformedCookieException {
        final List<Cookie> cookies = new ArrayList<Cookie>(elems.length);
        for (final HeaderElement headerelement : elems) {
            final String name = headerelement.getName();
            final String value = headerelement.getValue();
            if (name != null) {
                if (!name.isEmpty()) {
                    final BasicClientCookie cookie = new BasicClientCookie(name, value);
                    cookie.setPath(getDefaultPath(origin));
                    cookie.setDomain(getDefaultDomain(origin));
                    final NameValuePair[] attribs = headerelement.getParameters();
                    for (int j = attribs.length - 1; j >= 0; --j) {
                        final NameValuePair attrib = attribs[j];
                        final String s = attrib.getName().toLowerCase(Locale.ROOT);
                        cookie.setAttribute(s, attrib.getValue());
                        final CookieAttributeHandler handler = this.findAttribHandler(s);
                        if (handler != null) {
                            handler.parse(cookie, attrib.getValue());
                        }
                    }
                    cookies.add(cookie);
                }
            }
        }
        return cookies;
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        for (final CookieAttributeHandler handler : this.getAttribHandlers()) {
            handler.validate(cookie, origin);
        }
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        for (final CookieAttributeHandler handler : this.getAttribHandlers()) {
            if (!handler.match(cookie, origin)) {
                return false;
            }
        }
        return true;
    }
}
