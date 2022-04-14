package org.apache.http.auth;

import org.apache.http.protocol.*;

public interface AuthSchemeProvider
{
    AuthScheme create(final HttpContext p0);
}
