package org.apache.http.cookie;

import org.apache.http.protocol.*;

public interface CookieSpecProvider
{
    CookieSpec create(final HttpContext p0);
}
