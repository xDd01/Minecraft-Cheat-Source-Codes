package org.apache.http.client.methods;

import org.apache.http.*;
import java.io.*;

public interface CloseableHttpResponse extends HttpResponse, Closeable
{
}
