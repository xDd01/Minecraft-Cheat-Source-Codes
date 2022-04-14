package org.apache.http;

import org.apache.http.protocol.*;
import java.io.*;

public interface HttpRequestInterceptor
{
    void process(final HttpRequest p0, final HttpContext p1) throws HttpException, IOException;
}
