package org.apache.http.protocol;

import org.apache.http.*;

public interface HttpExpectationVerifier
{
    void verify(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws HttpException;
}
