package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public class BestMatchSpec extends DefaultCookieSpec
{
    public BestMatchSpec(final String[] datepatterns, final boolean oneHeader) {
        super(datepatterns, oneHeader);
    }
    
    public BestMatchSpec() {
        this(null, false);
    }
    
    @Override
    public String toString() {
        return "best-match";
    }
}
