package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.client.*;
import org.apache.http.protocol.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import java.net.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
class DefaultRedirectStrategyAdaptor implements RedirectStrategy
{
    private final RedirectHandler handler;
    
    public DefaultRedirectStrategyAdaptor(final RedirectHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        return this.handler.isRedirectRequested(response, context);
    }
    
    @Override
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        final URI uri = this.handler.getLocationURI(response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        return new HttpGet(uri);
    }
    
    public RedirectHandler getHandler() {
        return this.handler;
    }
}
