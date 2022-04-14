package org.apache.http.client;

import org.apache.http.*;
import org.apache.http.protocol.*;

public interface ServiceUnavailableRetryStrategy
{
    boolean retryRequest(final HttpResponse p0, final int p1, final HttpContext p2);
    
    long getRetryInterval();
}
