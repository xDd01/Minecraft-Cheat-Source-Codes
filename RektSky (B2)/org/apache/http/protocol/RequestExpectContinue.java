package org.apache.http.protocol;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import org.apache.http.*;
import java.io.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestExpectContinue implements HttpRequestInterceptor
{
    private final boolean activeByDefault;
    
    @Deprecated
    public RequestExpectContinue() {
        this(false);
    }
    
    public RequestExpectContinue(final boolean activeByDefault) {
        this.activeByDefault = activeByDefault;
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (!request.containsHeader("Expect") && request instanceof HttpEntityEnclosingRequest) {
            final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null && entity.getContentLength() != 0L && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                final boolean active = request.getParams().getBooleanParameter("http.protocol.expect-continue", this.activeByDefault);
                if (active) {
                    request.addHeader("Expect", "100-continue");
                }
            }
        }
    }
}
