package org.apache.http.impl.client;

import org.apache.http.client.*;
import org.apache.http.*;

public class NullBackoffStrategy implements ConnectionBackoffStrategy
{
    @Override
    public boolean shouldBackoff(final Throwable t) {
        return false;
    }
    
    @Override
    public boolean shouldBackoff(final HttpResponse resp) {
        return false;
    }
}
