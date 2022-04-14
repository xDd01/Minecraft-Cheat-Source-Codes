package org.apache.http;

import org.apache.http.protocol.*;

public interface ConnectionReuseStrategy
{
    boolean keepAlive(final HttpResponse p0, final HttpContext p1);
}
