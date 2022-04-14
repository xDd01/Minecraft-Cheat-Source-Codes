package org.apache.http.impl.client;

import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import java.net.*;
import org.apache.http.*;
import org.apache.http.params.*;
import org.apache.http.message.*;

@Deprecated
public class RequestWrapper extends AbstractHttpMessage implements HttpUriRequest
{
    private final HttpRequest original;
    private URI uri;
    private String method;
    private ProtocolVersion version;
    private int execCount;
    
    public RequestWrapper(final HttpRequest request) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        this.original = request;
        this.setParams(request.getParams());
        this.setHeaders(request.getAllHeaders());
        if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest)request).getURI();
            this.method = ((HttpUriRequest)request).getMethod();
            this.version = null;
        }
        else {
            final RequestLine requestLine = request.getRequestLine();
            try {
                this.uri = new URI(requestLine.getUri());
            }
            catch (URISyntaxException ex) {
                throw new ProtocolException("Invalid request URI: " + requestLine.getUri(), ex);
            }
            this.method = requestLine.getMethod();
            this.version = request.getProtocolVersion();
        }
        this.execCount = 0;
    }
    
    public void resetHeaders() {
        this.headergroup.clear();
        this.setHeaders(this.original.getAllHeaders());
    }
    
    @Override
    public String getMethod() {
        return this.method;
    }
    
    public void setMethod(final String method) {
        Args.notNull(method, "Method name");
        this.method = method;
    }
    
    @Override
    public ProtocolVersion getProtocolVersion() {
        if (this.version == null) {
            this.version = HttpProtocolParams.getVersion(this.getParams());
        }
        return this.version;
    }
    
    public void setProtocolVersion(final ProtocolVersion version) {
        this.version = version;
    }
    
    @Override
    public URI getURI() {
        return this.uri;
    }
    
    public void setURI(final URI uri) {
        this.uri = uri;
    }
    
    @Override
    public RequestLine getRequestLine() {
        final ProtocolVersion ver = this.getProtocolVersion();
        String uritext = null;
        if (this.uri != null) {
            uritext = this.uri.toASCIIString();
        }
        if (uritext == null || uritext.isEmpty()) {
            uritext = "/";
        }
        return new BasicRequestLine(this.getMethod(), uritext, ver);
    }
    
    @Override
    public void abort() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isAborted() {
        return false;
    }
    
    public HttpRequest getOriginal() {
        return this.original;
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public int getExecCount() {
        return this.execCount;
    }
    
    public void incrementExecCount() {
        ++this.execCount;
    }
}
