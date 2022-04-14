package org.apache.http.impl.client;

import org.apache.http.impl.*;
import org.apache.http.protocol.*;
import org.apache.http.message.*;
import org.apache.http.*;

public class DefaultClientConnectionReuseStrategy extends DefaultConnectionReuseStrategy
{
    public static final DefaultClientConnectionReuseStrategy INSTANCE;
    
    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        final HttpRequest request = (HttpRequest)context.getAttribute("http.request");
        if (request != null) {
            final Header[] connHeaders = request.getHeaders("Connection");
            if (connHeaders.length != 0) {
                final TokenIterator ti = new BasicTokenIterator(new BasicHeaderIterator(connHeaders, null));
                while (ti.hasNext()) {
                    final String token = ti.nextToken();
                    if ("Close".equalsIgnoreCase(token)) {
                        return false;
                    }
                }
            }
        }
        return super.keepAlive(response, context);
    }
    
    static {
        INSTANCE = new DefaultClientConnectionReuseStrategy();
    }
}
