package org.apache.http.client.protocol;

import org.apache.http.annotation.*;
import org.apache.commons.logging.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.http.conn.routing.*;
import org.apache.http.*;
import java.io.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestClientConnControl implements HttpRequestInterceptor
{
    private final Log log;
    private static final String PROXY_CONN_DIRECTIVE = "Proxy-Connection";
    
    public RequestClientConnControl() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            request.setHeader("Proxy-Connection", "Keep-Alive");
            return;
        }
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            this.log.debug("Connection route not set in the context");
            return;
        }
        if ((route.getHopCount() == 1 || route.isTunnelled()) && !request.containsHeader("Connection")) {
            request.addHeader("Connection", "Keep-Alive");
        }
        if (route.getHopCount() == 2 && !route.isTunnelled() && !request.containsHeader("Proxy-Connection")) {
            request.addHeader("Proxy-Connection", "Keep-Alive");
        }
    }
}
