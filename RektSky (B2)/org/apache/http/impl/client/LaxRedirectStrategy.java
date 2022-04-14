package org.apache.http.impl.client;

import org.apache.http.annotation.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxRedirectStrategy extends DefaultRedirectStrategy
{
    public static final LaxRedirectStrategy INSTANCE;
    private static final String[] REDIRECT_METHODS;
    
    @Override
    protected boolean isRedirectable(final String method) {
        for (final String m : LaxRedirectStrategy.REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        INSTANCE = new LaxRedirectStrategy();
        REDIRECT_METHODS = new String[] { "GET", "POST", "HEAD", "DELETE" };
    }
}
