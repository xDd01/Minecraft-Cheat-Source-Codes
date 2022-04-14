package org.apache.http.client.params;

import org.apache.http.params.*;
import org.apache.http.client.config.*;
import org.apache.http.*;
import java.net.*;
import java.util.*;

@Deprecated
public final class HttpClientParamConfig
{
    private HttpClientParamConfig() {
    }
    
    public static RequestConfig getRequestConfig(final HttpParams params) {
        return getRequestConfig(params, RequestConfig.DEFAULT);
    }
    
    public static RequestConfig getRequestConfig(final HttpParams params, final RequestConfig defaultConfig) {
        final RequestConfig.Builder builder = RequestConfig.copy(defaultConfig).setSocketTimeout(params.getIntParameter("http.socket.timeout", defaultConfig.getSocketTimeout())).setStaleConnectionCheckEnabled(params.getBooleanParameter("http.connection.stalecheck", defaultConfig.isStaleConnectionCheckEnabled())).setConnectTimeout(params.getIntParameter("http.connection.timeout", defaultConfig.getConnectTimeout())).setExpectContinueEnabled(params.getBooleanParameter("http.protocol.expect-continue", defaultConfig.isExpectContinueEnabled())).setAuthenticationEnabled(params.getBooleanParameter("http.protocol.handle-authentication", defaultConfig.isAuthenticationEnabled())).setCircularRedirectsAllowed(params.getBooleanParameter("http.protocol.allow-circular-redirects", defaultConfig.isCircularRedirectsAllowed())).setConnectionRequestTimeout((int)params.getLongParameter("http.conn-manager.timeout", defaultConfig.getConnectionRequestTimeout())).setMaxRedirects(params.getIntParameter("http.protocol.max-redirects", defaultConfig.getMaxRedirects())).setRedirectsEnabled(params.getBooleanParameter("http.protocol.handle-redirects", defaultConfig.isRedirectsEnabled())).setRelativeRedirectsAllowed(!params.getBooleanParameter("http.protocol.reject-relative-redirect", !defaultConfig.isRelativeRedirectsAllowed()));
        final HttpHost proxy = (HttpHost)params.getParameter("http.route.default-proxy");
        if (proxy != null) {
            builder.setProxy(proxy);
        }
        final InetAddress localAddress = (InetAddress)params.getParameter("http.route.local-address");
        if (localAddress != null) {
            builder.setLocalAddress(localAddress);
        }
        final Collection<String> targetAuthPrefs = (Collection<String>)params.getParameter("http.auth.target-scheme-pref");
        if (targetAuthPrefs != null) {
            builder.setTargetPreferredAuthSchemes(targetAuthPrefs);
        }
        final Collection<String> proxySuthPrefs = (Collection<String>)params.getParameter("http.auth.proxy-scheme-pref");
        if (proxySuthPrefs != null) {
            builder.setProxyPreferredAuthSchemes(proxySuthPrefs);
        }
        final String cookiePolicy = (String)params.getParameter("http.protocol.cookie-policy");
        if (cookiePolicy != null) {
            builder.setCookieSpec(cookiePolicy);
        }
        return builder.build();
    }
}
