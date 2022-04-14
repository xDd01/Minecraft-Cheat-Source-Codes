package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import java.io.*;
import org.apache.http.*;
import org.apache.http.client.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicResponseHandler extends AbstractResponseHandler<String>
{
    @Override
    public String handleEntity(final HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity);
    }
    
    @Override
    public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
        return super.handleResponse(response);
    }
}
