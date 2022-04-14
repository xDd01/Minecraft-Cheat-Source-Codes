package org.apache.http;

import org.apache.http.protocol.*;

public interface HttpResponseFactory
{
    HttpResponse newHttpResponse(final ProtocolVersion p0, final int p1, final HttpContext p2);
    
    HttpResponse newHttpResponse(final StatusLine p0, final HttpContext p1);
}
