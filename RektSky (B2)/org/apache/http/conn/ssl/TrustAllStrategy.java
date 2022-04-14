package org.apache.http.conn.ssl;

import java.security.cert.*;

public class TrustAllStrategy implements TrustStrategy
{
    public static final TrustAllStrategy INSTANCE;
    
    @Override
    public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        return true;
    }
    
    static {
        INSTANCE = new TrustAllStrategy();
    }
}
