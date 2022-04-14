package org.apache.http.conn.ssl;

import org.apache.http.annotation.*;
import javax.net.ssl.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoopHostnameVerifier implements HostnameVerifier
{
    public static final NoopHostnameVerifier INSTANCE;
    
    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        return true;
    }
    
    @Override
    public final String toString() {
        return "NO_OP";
    }
    
    static {
        INSTANCE = new NoopHostnameVerifier();
    }
}
