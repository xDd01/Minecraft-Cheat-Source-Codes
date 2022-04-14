package org.apache.http.conn.ssl;

import org.apache.http.annotation.*;
import javax.net.ssl.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class StrictHostnameVerifier extends AbstractVerifier
{
    public static final StrictHostnameVerifier INSTANCE;
    
    @Override
    public final void verify(final String host, final String[] cns, final String[] subjectAlts) throws SSLException {
        this.verify(host, cns, subjectAlts, true);
    }
    
    @Override
    public final String toString() {
        return "STRICT";
    }
    
    static {
        INSTANCE = new StrictHostnameVerifier();
    }
}
