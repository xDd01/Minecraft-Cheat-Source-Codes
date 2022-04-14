package org.apache.http.conn.params;

import org.apache.http.params.*;
import org.apache.http.*;
import java.net.*;
import org.apache.http.conn.routing.*;

@Deprecated
public class ConnRouteParamBean extends HttpAbstractParamBean
{
    public ConnRouteParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setDefaultProxy(final HttpHost defaultProxy) {
        this.params.setParameter("http.route.default-proxy", defaultProxy);
    }
    
    public void setLocalAddress(final InetAddress address) {
        this.params.setParameter("http.route.local-address", address);
    }
    
    public void setForcedRoute(final HttpRoute route) {
        this.params.setParameter("http.route.forced-route", route);
    }
}
