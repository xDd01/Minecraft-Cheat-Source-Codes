package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.cookie.*;
import org.apache.http.protocol.*;

@Obsolete
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NetscapeDraftSpecProvider implements CookieSpecProvider
{
    private final String[] datepatterns;
    private volatile CookieSpec cookieSpec;
    
    public NetscapeDraftSpecProvider(final String[] datepatterns) {
        this.datepatterns = datepatterns;
    }
    
    public NetscapeDraftSpecProvider() {
        this(null);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new NetscapeDraftSpec(this.datepatterns);
                }
            }
        }
        return this.cookieSpec;
    }
}
