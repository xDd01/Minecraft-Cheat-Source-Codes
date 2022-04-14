package org.apache.http;

import org.apache.http.protocol.*;
import java.io.*;

public interface HttpResponseInterceptor
{
    void process(final HttpResponse p0, final HttpContext p1) throws HttpException, IOException;
}
