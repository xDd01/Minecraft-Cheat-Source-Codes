package org.apache.http.conn;

import org.apache.http.*;
import org.apache.http.config.*;

public interface HttpConnectionFactory<T, C extends HttpConnection>
{
    C create(final T p0, final ConnectionConfig p1);
}
