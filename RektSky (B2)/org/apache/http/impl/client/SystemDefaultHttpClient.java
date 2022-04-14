package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.params.*;
import org.apache.http.conn.*;
import org.apache.http.conn.routing.*;
import java.net.*;
import org.apache.http.impl.conn.*;
import org.apache.http.*;
import org.apache.http.impl.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class SystemDefaultHttpClient extends DefaultHttpClient
{
    public SystemDefaultHttpClient(final HttpParams params) {
        super(null, params);
    }
    
    public SystemDefaultHttpClient() {
        super(null, null);
    }
    
    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        final PoolingClientConnectionManager connmgr = new PoolingClientConnectionManager(SchemeRegistryFactory.createSystemDefault());
        String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
            s = System.getProperty("http.maxConnections", "5");
            final int max = Integer.parseInt(s);
            connmgr.setDefaultMaxPerRoute(max);
            connmgr.setMaxTotal(2 * max);
        }
        return connmgr;
    }
    
    @Override
    protected HttpRoutePlanner createHttpRoutePlanner() {
        return new ProxySelectorRoutePlanner(this.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
    }
    
    @Override
    protected ConnectionReuseStrategy createConnectionReuseStrategy() {
        final String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
            return new DefaultConnectionReuseStrategy();
        }
        return new NoConnectionReuseStrategy();
    }
}
