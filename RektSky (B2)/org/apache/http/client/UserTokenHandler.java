package org.apache.http.client;

import org.apache.http.protocol.*;

public interface UserTokenHandler
{
    Object getUserToken(final HttpContext p0);
}
