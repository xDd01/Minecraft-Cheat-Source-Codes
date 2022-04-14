package org.apache.http.protocol;

import org.apache.http.*;
import java.io.*;

public interface HttpRequestHandler
{
    void handle(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws HttpException, IOException;
}
