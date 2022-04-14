package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.client.config.*;
import org.apache.http.protocol.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.auth.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class TargetAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public static final TargetAuthenticationStrategy INSTANCE;
    
    public TargetAuthenticationStrategy() {
        super(401, "WWW-Authenticate");
    }
    
    @Override
    Collection<String> getPreferredAuthSchemes(final RequestConfig config) {
        return config.getTargetPreferredAuthSchemes();
    }
    
    static {
        INSTANCE = new TargetAuthenticationStrategy();
    }
}
