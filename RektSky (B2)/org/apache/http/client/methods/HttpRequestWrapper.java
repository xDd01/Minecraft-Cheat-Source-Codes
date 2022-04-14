package org.apache.http.client.methods;

import java.net.*;
import org.apache.http.util.*;
import org.apache.http.message.*;
import org.apache.http.params.*;
import org.apache.http.*;

public class HttpRequestWrapper extends AbstractHttpMessage implements HttpUriRequest
{
    private final HttpRequest original;
    private final HttpHost target;
    private final String method;
    private RequestLine requestLine;
    private ProtocolVersion version;
    private URI uri;
    
    private HttpRequestWrapper(final HttpRequest request, final HttpHost target) {
        this.original = Args.notNull(request, "HTTP request");
        this.target = target;
        this.version = this.original.getRequestLine().getProtocolVersion();
        this.method = this.original.getRequestLine().getMethod();
        if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest)request).getURI();
        }
        else {
            this.uri = null;
        }
        this.setHeaders(request.getAllHeaders());
    }
    
    @Override
    public ProtocolVersion getProtocolVersion() {
        return (this.version != null) ? this.version : this.original.getProtocolVersion();
    }
    
    public void setProtocolVersion(final ProtocolVersion version) {
        this.version = version;
        this.requestLine = null;
    }
    
    @Override
    public URI getURI() {
        return this.uri;
    }
    
    public void setURI(final URI uri) {
        this.uri = uri;
        this.requestLine = null;
    }
    
    @Override
    public String getMethod() {
        return this.method;
    }
    
    @Override
    public void abort() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isAborted() {
        return false;
    }
    
    @Override
    public RequestLine getRequestLine() {
        if (this.requestLine == null) {
            String requestUri;
            if (this.uri != null) {
                requestUri = this.uri.toASCIIString();
            }
            else {
                requestUri = this.original.getRequestLine().getUri();
            }
            if (requestUri == null || requestUri.isEmpty()) {
                requestUri = "/";
            }
            this.requestLine = new BasicRequestLine(this.method, requestUri, this.getProtocolVersion());
        }
        return this.requestLine;
    }
    
    public HttpRequest getOriginal() {
        return this.original;
    }
    
    public HttpHost getTarget() {
        return this.target;
    }
    
    @Override
    public String toString() {
        return this.getRequestLine() + " " + this.headergroup;
    }
    
    public static HttpRequestWrapper wrap(final HttpRequest request) {
        return wrap(request, null);
    }
    
    public static HttpRequestWrapper wrap(final HttpRequest request, final HttpHost target) {
        Args.notNull(request, "HTTP request");
        if (request instanceof HttpEntityEnclosingRequest) {
            return new HttpEntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request, target);
        }
        return new HttpRequestWrapper(request, target);
    }
    
    @Deprecated
    @Override
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = this.original.getParams().copy();
        }
        return this.params;
    }
    
    static class HttpEntityEnclosingRequestWrapper extends HttpRequestWrapper implements HttpEntityEnclosingRequest
    {
        private HttpEntity entity;
        
        HttpEntityEnclosingRequestWrapper(final HttpEntityEnclosingRequest request, final HttpHost target) {
            super(request, target, null);
            this.entity = request.getEntity();
        }
        
        @Override
        public HttpEntity getEntity() {
            return this.entity;
        }
        
        @Override
        public void setEntity(final HttpEntity entity) {
            this.entity = entity;
        }
        
        @Override
        public boolean expectContinue() {
            final Header expect = this.getFirstHeader("Expect");
            return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
        }
    }
}
