package org.apache.http.conn.routing;

import org.apache.http.protocol.*;
import org.apache.http.*;

public interface HttpRoutePlanner
{
    HttpRoute determineRoute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws HttpException;
}
