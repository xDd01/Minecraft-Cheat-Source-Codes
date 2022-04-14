package org.apache.http.client.methods;

import java.net.*;

public class HttpGet extends HttpRequestBase
{
    public static final String METHOD_NAME = "GET";
    
    public HttpGet() {
    }
    
    public HttpGet(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpGet(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    @Override
    public String getMethod() {
        return "GET";
    }
}
