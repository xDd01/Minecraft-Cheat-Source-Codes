package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.cookie.*;
import org.apache.http.params.*;
import java.util.*;
import org.apache.http.protocol.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RFC2965SpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    private final CookieSpec cookieSpec;
    
    public RFC2965SpecFactory(final String[] datepatterns, final boolean oneHeader) {
        this.cookieSpec = new RFC2965Spec(datepatterns, oneHeader);
    }
    
    public RFC2965SpecFactory() {
        this(null, false);
    }
    
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        if (params != null) {
            String[] patterns = null;
            final Collection<?> param = (Collection<?>)params.getParameter("http.protocol.cookie-datepatterns");
            if (param != null) {
                patterns = new String[param.size()];
                patterns = param.toArray(patterns);
            }
            final boolean singleHeader = params.getBooleanParameter("http.protocol.single-cookie-header", false);
            return new RFC2965Spec(patterns, singleHeader);
        }
        return new RFC2965Spec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return this.cookieSpec;
    }
}
