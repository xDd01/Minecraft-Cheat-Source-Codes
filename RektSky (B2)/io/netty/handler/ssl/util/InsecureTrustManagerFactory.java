package io.netty.handler.ssl.util;

import java.security.*;
import io.netty.util.internal.logging.*;
import javax.net.ssl.*;
import java.security.cert.*;
import io.netty.util.internal.*;

public final class InsecureTrustManagerFactory extends SimpleTrustManagerFactory
{
    private static final InternalLogger logger;
    public static final TrustManagerFactory INSTANCE;
    private static final TrustManager tm;
    
    private InsecureTrustManagerFactory() {
    }
    
    @Override
    protected void engineInit(final KeyStore keyStore) throws Exception {
    }
    
    @Override
    protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }
    
    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[] { InsecureTrustManagerFactory.tm };
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(InsecureTrustManagerFactory.class);
        INSTANCE = new InsecureTrustManagerFactory();
        tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String s) {
                InsecureTrustManagerFactory.logger.debug("Accepting a client certificate: " + chain[0].getSubjectDN());
            }
            
            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String s) {
                InsecureTrustManagerFactory.logger.debug("Accepting a server certificate: " + chain[0].getSubjectDN());
            }
            
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return EmptyArrays.EMPTY_X509_CERTIFICATES;
            }
        };
    }
}
