package org.apache.http.auth;

import org.apache.http.protocol.*;
import org.apache.http.*;

public interface ContextAwareAuthScheme extends AuthScheme
{
    Header authenticate(final Credentials p0, final HttpRequest p1, final HttpContext p2) throws AuthenticationException;
}
