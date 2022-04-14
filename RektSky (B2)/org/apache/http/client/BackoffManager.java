package org.apache.http.client;

import org.apache.http.conn.routing.*;

public interface BackoffManager
{
    void backOff(final HttpRoute p0);
    
    void probe(final HttpRoute p0);
}
