package org.apache.http.client;

import org.apache.http.protocol.*;
import org.apache.http.*;
import java.io.*;

@Deprecated
public interface RequestDirector
{
    HttpResponse execute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws HttpException, IOException;
}
