package org.apache.http.client;

import org.apache.http.*;
import java.io.*;

public interface ResponseHandler<T>
{
    T handleResponse(final HttpResponse p0) throws ClientProtocolException, IOException;
}
