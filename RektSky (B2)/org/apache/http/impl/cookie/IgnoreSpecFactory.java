package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.params.*;
import org.apache.http.cookie.*;
import org.apache.http.protocol.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        return new IgnoreSpec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return new IgnoreSpec();
    }
}
