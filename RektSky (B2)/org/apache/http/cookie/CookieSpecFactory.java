package org.apache.http.cookie;

import org.apache.http.params.*;

@Deprecated
public interface CookieSpecFactory
{
    CookieSpec newInstance(final HttpParams p0);
}
