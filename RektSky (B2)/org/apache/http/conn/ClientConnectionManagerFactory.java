package org.apache.http.conn;

import org.apache.http.params.*;
import org.apache.http.conn.scheme.*;

@Deprecated
public interface ClientConnectionManagerFactory
{
    ClientConnectionManager newInstance(final HttpParams p0, final SchemeRegistry p1);
}
