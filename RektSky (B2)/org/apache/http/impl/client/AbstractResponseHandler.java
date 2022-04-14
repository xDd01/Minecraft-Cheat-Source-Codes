package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import org.apache.http.client.*;
import org.apache.http.*;
import java.io.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public abstract class AbstractResponseHandler<T> implements ResponseHandler<T>
{
    @Override
    public T handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return (entity == null) ? null : this.handleEntity(entity);
    }
    
    public abstract T handleEntity(final HttpEntity p0) throws IOException;
}
