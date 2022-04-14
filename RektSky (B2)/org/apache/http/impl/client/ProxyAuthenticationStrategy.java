package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.client.config.*;
import org.apache.http.protocol.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.auth.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public static final ProxyAuthenticationStrategy INSTANCE;
    
    public ProxyAuthenticationStrategy() {
        super(407, "Proxy-Authenticate");
    }
    
    @Override
    Collection<String> getPreferredAuthSchemes(final RequestConfig config) {
        return config.getProxyPreferredAuthSchemes();
    }
    
    static {
        INSTANCE = new ProxyAuthenticationStrategy();
    }
}
