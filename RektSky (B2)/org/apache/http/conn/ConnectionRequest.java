package org.apache.http.conn;

import org.apache.http.concurrent.*;
import org.apache.http.*;
import java.util.concurrent.*;

public interface ConnectionRequest extends Cancellable
{
    HttpClientConnection get(final long p0, final TimeUnit p1) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException;
}
