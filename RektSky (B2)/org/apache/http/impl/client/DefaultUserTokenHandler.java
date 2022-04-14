package org.apache.http.impl.client;

import org.apache.http.client.*;
import org.apache.http.annotation.*;
import org.apache.http.protocol.*;
import org.apache.http.client.protocol.*;
import org.apache.http.conn.*;
import java.security.*;
import org.apache.http.*;
import javax.net.ssl.*;
import org.apache.http.auth.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultUserTokenHandler implements UserTokenHandler
{
    public static final DefaultUserTokenHandler INSTANCE;
    
    @Override
    public Object getUserToken(final HttpContext context) {
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        Principal userPrincipal = null;
        final AuthState targetAuthState = clientContext.getTargetAuthState();
        if (targetAuthState != null) {
            userPrincipal = getAuthPrincipal(targetAuthState);
            if (userPrincipal == null) {
                final AuthState proxyAuthState = clientContext.getProxyAuthState();
                userPrincipal = getAuthPrincipal(proxyAuthState);
            }
        }
        if (userPrincipal == null) {
            final HttpConnection conn = clientContext.getConnection();
            if (conn.isOpen() && conn instanceof ManagedHttpClientConnection) {
                final SSLSession sslsession = ((ManagedHttpClientConnection)conn).getSSLSession();
                if (sslsession != null) {
                    userPrincipal = sslsession.getLocalPrincipal();
                }
            }
        }
        return userPrincipal;
    }
    
    private static Principal getAuthPrincipal(final AuthState authState) {
        final AuthScheme scheme = authState.getAuthScheme();
        if (scheme != null && scheme.isComplete() && scheme.isConnectionBased()) {
            final Credentials creds = authState.getCredentials();
            if (creds != null) {
                return creds.getUserPrincipal();
            }
        }
        return null;
    }
    
    static {
        INSTANCE = new DefaultUserTokenHandler();
    }
}
