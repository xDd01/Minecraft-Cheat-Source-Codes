package org.apache.http.conn.params;

import org.apache.http.conn.routing.*;

@Deprecated
public interface ConnPerRoute
{
    int getMaxForRoute(final HttpRoute p0);
}
