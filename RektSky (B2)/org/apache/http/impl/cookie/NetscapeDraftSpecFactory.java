package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.cookie.*;
import org.apache.http.params.*;
import java.util.*;
import org.apache.http.protocol.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NetscapeDraftSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    private final CookieSpec cookieSpec;
    
    public NetscapeDraftSpecFactory(final String[] datepatterns) {
        this.cookieSpec = new NetscapeDraftSpec(datepatterns);
    }
    
    public NetscapeDraftSpecFactory() {
        this(null);
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
            return new NetscapeDraftSpec(patterns);
        }
        return new NetscapeDraftSpec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return this.cookieSpec;
    }
}
