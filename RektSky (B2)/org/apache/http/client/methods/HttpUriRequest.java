package org.apache.http.client.methods;

import org.apache.http.*;
import java.net.*;

public interface HttpUriRequest extends HttpRequest
{
    String getMethod();
    
    URI getURI();
    
    void abort() throws UnsupportedOperationException;
    
    boolean isAborted();
}
