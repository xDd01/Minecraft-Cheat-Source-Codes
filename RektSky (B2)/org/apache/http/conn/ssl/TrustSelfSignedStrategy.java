package org.apache.http.conn.ssl;

import java.security.cert.*;

public class TrustSelfSignedStrategy implements TrustStrategy
{
    public static final TrustSelfSignedStrategy INSTANCE;
    
    @Override
    public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        return chain.length == 1;
    }
    
    static {
        INSTANCE = new TrustSelfSignedStrategy();
    }
}
