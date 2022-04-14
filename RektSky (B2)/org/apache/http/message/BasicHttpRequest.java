package org.apache.http.message;

import org.apache.http.util.*;
import org.apache.http.*;

public class BasicHttpRequest extends AbstractHttpMessage implements HttpRequest
{
    private final String method;
    private final String uri;
    private RequestLine requestline;
    
    public BasicHttpRequest(final String method, final String uri) {
        this.method = Args.notNull(method, "Method name");
        this.uri = Args.notNull(uri, "Request URI");
        this.requestline = null;
    }
    
    public BasicHttpRequest(final String method, final String uri, final ProtocolVersion ver) {
        this(new BasicRequestLine(method, uri, ver));
    }
    
    public BasicHttpRequest(final RequestLine requestline) {
        this.requestline = Args.notNull(requestline, "Request line");
        this.method = requestline.getMethod();
        this.uri = requestline.getUri();
    }
    
    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.getRequestLine().getProtocolVersion();
    }
    
    @Override
    public RequestLine getRequestLine() {
        if (this.requestline == null) {
            this.requestline = new BasicRequestLine(this.method, this.uri, HttpVersion.HTTP_1_1);
        }
        return this.requestline;
    }
    
    @Override
    public String toString() {
        return this.method + ' ' + this.uri + ' ' + this.headergroup;
    }
}
