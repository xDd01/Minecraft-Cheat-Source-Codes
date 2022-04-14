package org.apache.http.conn.ssl;

import org.apache.http.annotation.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class AllowAllHostnameVerifier extends AbstractVerifier
{
    public static final AllowAllHostnameVerifier INSTANCE;
    
    @Override
    public final void verify(final String host, final String[] cns, final String[] subjectAlts) {
    }
    
    @Override
    public final String toString() {
        return "ALLOW_ALL";
    }
    
    static {
        INSTANCE = new AllowAllHostnameVerifier();
    }
}
