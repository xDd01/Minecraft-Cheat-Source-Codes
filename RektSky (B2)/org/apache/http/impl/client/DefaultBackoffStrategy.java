package org.apache.http.impl.client;

import org.apache.http.client.*;
import java.net.*;
import org.apache.http.*;

public class DefaultBackoffStrategy implements ConnectionBackoffStrategy
{
    @Override
    public boolean shouldBackoff(final Throwable t) {
        return t instanceof SocketTimeoutException || t instanceof ConnectException;
    }
    
    @Override
    public boolean shouldBackoff(final HttpResponse resp) {
        return resp.getStatusLine().getStatusCode() == 503;
    }
}
