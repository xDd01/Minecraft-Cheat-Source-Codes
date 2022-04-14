package org.apache.http.client.methods;

import java.net.*;

public class HttpHead extends HttpRequestBase
{
    public static final String METHOD_NAME = "HEAD";
    
    public HttpHead() {
    }
    
    public HttpHead(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpHead(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    @Override
    public String getMethod() {
        return "HEAD";
    }
}
