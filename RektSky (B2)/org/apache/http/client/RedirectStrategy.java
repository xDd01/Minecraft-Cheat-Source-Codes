package org.apache.http.client;

import org.apache.http.protocol.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;

public interface RedirectStrategy
{
    boolean isRedirected(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws ProtocolException;
    
    HttpUriRequest getRedirect(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws ProtocolException;
}
