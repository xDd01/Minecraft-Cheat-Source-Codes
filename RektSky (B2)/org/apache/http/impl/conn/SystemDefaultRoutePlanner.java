package org.apache.http.impl.conn;

import org.apache.http.annotation.*;
import org.apache.http.conn.*;
import org.apache.http.protocol.*;
import org.apache.http.*;
import java.net.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class SystemDefaultRoutePlanner extends DefaultRoutePlanner
{
    private final ProxySelector proxySelector;
    
    public SystemDefaultRoutePlanner(final SchemePortResolver schemePortResolver, final ProxySelector proxySelector) {
        super(schemePortResolver);
        this.proxySelector = proxySelector;
    }
    
    public SystemDefaultRoutePlanner(final ProxySelector proxySelector) {
        this(null, proxySelector);
    }
    
    @Override
    protected HttpHost determineProxy(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
        URI targetURI;
        try {
            targetURI = new URI(target.toURI());
        }
        catch (URISyntaxException ex) {
            throw new HttpException("Cannot convert host to URI: " + target, ex);
        }
        ProxySelector proxySelectorInstance = this.proxySelector;
        if (proxySelectorInstance == null) {
            proxySelectorInstance = ProxySelector.getDefault();
        }
        if (proxySelectorInstance == null) {
            return null;
        }
        final List<Proxy> proxies = proxySelectorInstance.select(targetURI);
        final Proxy p = this.chooseProxy(proxies);
        HttpHost result = null;
        if (p.type() == Proxy.Type.HTTP) {
            if (!(p.address() instanceof InetSocketAddress)) {
                throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
            }
            final InetSocketAddress isa = (InetSocketAddress)p.address();
            result = new HttpHost(this.getHost(isa), isa.getPort());
        }
        return result;
    }
    
    private String getHost(final InetSocketAddress isa) {
        return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
    }
    
    private Proxy chooseProxy(final List<Proxy> proxies) {
        Proxy result = null;
        for (int i = 0; result == null && i < proxies.size(); ++i) {
            final Proxy p = proxies.get(i);
            switch (p.type()) {
                case DIRECT:
                case HTTP: {
                    result = p;
                    break;
                }
            }
        }
        if (result == null) {
            result = Proxy.NO_PROXY;
        }
        return result;
    }
}
