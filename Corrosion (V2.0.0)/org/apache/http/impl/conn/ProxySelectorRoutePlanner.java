/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
@NotThreadSafe
public class ProxySelectorRoutePlanner
implements HttpRoutePlanner {
    protected final SchemeRegistry schemeRegistry;
    protected ProxySelector proxySelector;

    public ProxySelectorRoutePlanner(SchemeRegistry schreg, ProxySelector prosel) {
        Args.notNull(schreg, "SchemeRegistry");
        this.schemeRegistry = schreg;
        this.proxySelector = prosel;
    }

    public ProxySelector getProxySelector() {
        return this.proxySelector;
    }

    public void setProxySelector(ProxySelector prosel) {
        this.proxySelector = prosel;
    }

    @Override
    public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        Args.notNull(request, "HTTP request");
        HttpRoute route = ConnRouteParams.getForcedRoute(request.getParams());
        if (route != null) {
            return route;
        }
        Asserts.notNull(target, "Target host");
        InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
        HttpHost proxy = this.determineProxy(target, request, context);
        Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
        boolean secure = schm.isLayered();
        route = proxy == null ? new HttpRoute(target, local, secure) : new HttpRoute(target, local, proxy, secure);
        return route;
    }

    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        ProxySelector psel = this.proxySelector;
        if (psel == null) {
            psel = ProxySelector.getDefault();
        }
        if (psel == null) {
            return null;
        }
        URI targetURI = null;
        try {
            targetURI = new URI(target.toURI());
        }
        catch (URISyntaxException usx) {
            throw new HttpException("Cannot convert host to URI: " + target, usx);
        }
        List<Proxy> proxies = psel.select(targetURI);
        Proxy p2 = this.chooseProxy(proxies, target, request, context);
        HttpHost result = null;
        if (p2.type() == Proxy.Type.HTTP) {
            if (!(p2.address() instanceof InetSocketAddress)) {
                throw new HttpException("Unable to handle non-Inet proxy address: " + p2.address());
            }
            InetSocketAddress isa = (InetSocketAddress)p2.address();
            result = new HttpHost(this.getHost(isa), isa.getPort());
        }
        return result;
    }

    protected String getHost(InetSocketAddress isa) {
        return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
    }

    protected Proxy chooseProxy(List<Proxy> proxies, HttpHost target, HttpRequest request, HttpContext context) {
        Args.notEmpty(proxies, "List of proxies");
        Proxy result = null;
        block3: for (int i2 = 0; result == null && i2 < proxies.size(); ++i2) {
            Proxy p2 = proxies.get(i2);
            switch (p2.type()) {
                case DIRECT: 
                case HTTP: {
                    result = p2;
                    continue block3;
                }
            }
        }
        if (result == null) {
            result = Proxy.NO_PROXY;
        }
        return result;
    }
}

