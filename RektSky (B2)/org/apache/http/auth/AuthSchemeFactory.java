package org.apache.http.auth;

import org.apache.http.params.*;

@Deprecated
public interface AuthSchemeFactory
{
    AuthScheme newInstance(final HttpParams p0);
}
