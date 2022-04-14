package org.apache.http.impl.execchain;

import org.apache.http.conn.routing.*;
import org.apache.http.client.protocol.*;
import org.apache.http.client.methods.*;
import java.io.*;
import org.apache.http.*;

public interface ClientExecChain
{
    CloseableHttpResponse execute(final HttpRoute p0, final HttpRequestWrapper p1, final HttpClientContext p2, final HttpExecutionAware p3) throws IOException, HttpException;
}
