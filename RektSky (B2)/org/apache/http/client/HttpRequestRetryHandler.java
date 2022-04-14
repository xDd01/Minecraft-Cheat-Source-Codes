package org.apache.http.client;

import java.io.*;
import org.apache.http.protocol.*;

public interface HttpRequestRetryHandler
{
    boolean retryRequest(final IOException p0, final int p1, final HttpContext p2);
}
