package org.apache.http.conn;

import org.apache.http.*;
import org.apache.http.protocol.*;

public interface ConnectionKeepAliveStrategy
{
    long getKeepAliveDuration(final HttpResponse p0, final HttpContext p1);
}
