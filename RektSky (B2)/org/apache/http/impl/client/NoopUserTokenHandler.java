package org.apache.http.impl.client;

import org.apache.http.client.*;
import org.apache.http.annotation.*;
import org.apache.http.protocol.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoopUserTokenHandler implements UserTokenHandler
{
    public static final NoopUserTokenHandler INSTANCE;
    
    @Override
    public Object getUserToken(final HttpContext context) {
        return null;
    }
    
    static {
        INSTANCE = new NoopUserTokenHandler();
    }
}
