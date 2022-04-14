package org.apache.http.client.methods;

import java.nio.charset.*;
import org.apache.http.client.config.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import java.io.*;
import org.apache.http.*;
import java.util.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.utils.*;
import java.net.*;

public class RequestBuilder
{
    private String method;
    private Charset charset;
    private ProtocolVersion version;
    private URI uri;
    private HeaderGroup headergroup;
    private HttpEntity entity;
    private List<NameValuePair> parameters;
    private RequestConfig config;
    
    RequestBuilder(final String method) {
        this.charset = Consts.UTF_8;
        this.method = method;
    }
    
    RequestBuilder(final String method, final URI uri) {
        this.method = method;
        this.uri = uri;
    }
    
    RequestBuilder(final String method, final String uri) {
        this.method = method;
        this.uri = ((uri != null) ? URI.create(uri) : null);
    }
    
    RequestBuilder() {
        this(null);
    }
    
    public static RequestBuilder create(final String method) {
        Args.notBlank(method, "HTTP method");
        return new RequestBuilder(method);
    }
    
    public static RequestBuilder get() {
        return new RequestBuilder("GET");
    }
    
    public static RequestBuilder get(final URI uri) {
        return new RequestBuilder("GET", uri);
    }
    
    public static RequestBuilder get(final String uri) {
        return new RequestBuilder("GET", uri);
    }
    
    public static RequestBuilder head() {
        return new RequestBuilder("HEAD");
    }
    
    public static RequestBuilder head(final URI uri) {
        return new RequestBuilder("HEAD", uri);
    }
    
    public static RequestBuilder head(final String uri) {
        return new RequestBuilder("HEAD", uri);
    }
    
    public static RequestBuilder patch() {
        return new RequestBuilder("PATCH");
    }
    
    public static RequestBuilder patch(final URI uri) {
        return new RequestBuilder("PATCH", uri);
    }
    
    public static RequestBuilder patch(final String uri) {
        return new RequestBuilder("PATCH", uri);
    }
    
    public static RequestBuilder post() {
        return new RequestBuilder("POST");
    }
    
    public static RequestBuilder post(final URI uri) {
        return new RequestBuilder("POST", uri);
    }
    
    public static RequestBuilder post(final String uri) {
        return new RequestBuilder("POST", uri);
    }
    
    public static RequestBuilder put() {
        return new RequestBuilder("PUT");
    }
    
    public static RequestBuilder put(final URI uri) {
        return new RequestBuilder("PUT", uri);
    }
    
    public static RequestBuilder put(final String uri) {
        return new RequestBuilder("PUT", uri);
    }
    
    public static RequestBuilder delete() {
        return new RequestBuilder("DELETE");
    }
    
    public static RequestBuilder delete(final URI uri) {
        return new RequestBuilder("DELETE", uri);
    }
    
    public static RequestBuilder delete(final String uri) {
        return new RequestBuilder("DELETE", uri);
    }
    
    public static RequestBuilder trace() {
        return new RequestBuilder("TRACE");
    }
    
    public static RequestBuilder trace(final URI uri) {
        return new RequestBuilder("TRACE", uri);
    }
    
    public static RequestBuilder trace(final String uri) {
        return new RequestBuilder("TRACE", uri);
    }
    
    public static RequestBuilder options() {
        return new RequestBuilder("OPTIONS");
    }
    
    public static RequestBuilder options(final URI uri) {
        return new RequestBuilder("OPTIONS", uri);
    }
    
    public static RequestBuilder options(final String uri) {
        return new RequestBuilder("OPTIONS", uri);
    }
    
    public static RequestBuilder copy(final HttpRequest request) {
        Args.notNull(request, "HTTP request");
        return new RequestBuilder().doCopy(request);
    }
    
    private RequestBuilder doCopy(final HttpRequest request) {
        if (request == null) {
            return this;
        }
        this.method = request.getRequestLine().getMethod();
        this.version = request.getRequestLine().getProtocolVersion();
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.clear();
        this.headergroup.setHeaders(request.getAllHeaders());
        this.parameters = null;
        this.entity = null;
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntity originalEntity = ((HttpEntityEnclosingRequest)request).getEntity();
            final ContentType contentType = ContentType.get(originalEntity);
            if (contentType != null && contentType.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
                try {
                    final List<NameValuePair> formParams = URLEncodedUtils.parse(originalEntity);
                    if (!formParams.isEmpty()) {
                        this.parameters = formParams;
                    }
                }
                catch (IOException ignore) {}
            }
            else {
                this.entity = originalEntity;
            }
        }
        if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest)request).getURI();
        }
        else {
            this.uri = URI.create(request.getRequestLine().getUri());
        }
        if (request instanceof Configurable) {
            this.config = ((Configurable)request).getConfig();
        }
        else {
            this.config = null;
        }
        return this;
    }
    
    public RequestBuilder setCharset(final Charset charset) {
        this.charset = charset;
        return this;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public ProtocolVersion getVersion() {
        return this.version;
    }
    
    public RequestBuilder setVersion(final ProtocolVersion version) {
        this.version = version;
        return this;
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public RequestBuilder setUri(final URI uri) {
        this.uri = uri;
        return this;
    }
    
    public RequestBuilder setUri(final String uri) {
        this.uri = ((uri != null) ? URI.create(uri) : null);
        return this;
    }
    
    public Header getFirstHeader(final String name) {
        return (this.headergroup != null) ? this.headergroup.getFirstHeader(name) : null;
    }
    
    public Header getLastHeader(final String name) {
        return (this.headergroup != null) ? this.headergroup.getLastHeader(name) : null;
    }
    
    public Header[] getHeaders(final String name) {
        return (Header[])((this.headergroup != null) ? this.headergroup.getHeaders(name) : null);
    }
    
    public RequestBuilder addHeader(final Header header) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.addHeader(header);
        return this;
    }
    
    public RequestBuilder addHeader(final String name, final String value) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.addHeader(new BasicHeader(name, value));
        return this;
    }
    
    public RequestBuilder removeHeader(final Header header) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.removeHeader(header);
        return this;
    }
    
    public RequestBuilder removeHeaders(final String name) {
        if (name == null || this.headergroup == null) {
            return this;
        }
        final HeaderIterator i = this.headergroup.iterator();
        while (i.hasNext()) {
            final Header header = i.nextHeader();
            if (name.equalsIgnoreCase(header.getName())) {
                i.remove();
            }
        }
        return this;
    }
    
    public RequestBuilder setHeader(final Header header) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.updateHeader(header);
        return this;
    }
    
    public RequestBuilder setHeader(final String name, final String value) {
        if (this.headergroup == null) {
            this.headergroup = new HeaderGroup();
        }
        this.headergroup.updateHeader(new BasicHeader(name, value));
        return this;
    }
    
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    public RequestBuilder setEntity(final HttpEntity entity) {
        this.entity = entity;
        return this;
    }
    
    public List<NameValuePair> getParameters() {
        return (this.parameters != null) ? new ArrayList<NameValuePair>(this.parameters) : new ArrayList<NameValuePair>();
    }
    
    public RequestBuilder addParameter(final NameValuePair nvp) {
        Args.notNull(nvp, "Name value pair");
        if (this.parameters == null) {
            this.parameters = new LinkedList<NameValuePair>();
        }
        this.parameters.add(nvp);
        return this;
    }
    
    public RequestBuilder addParameter(final String name, final String value) {
        return this.addParameter(new BasicNameValuePair(name, value));
    }
    
    public RequestBuilder addParameters(final NameValuePair... nvps) {
        for (final NameValuePair nvp : nvps) {
            this.addParameter(nvp);
        }
        return this;
    }
    
    public RequestConfig getConfig() {
        return this.config;
    }
    
    public RequestBuilder setConfig(final RequestConfig config) {
        this.config = config;
        return this;
    }
    
    public HttpUriRequest build() {
        URI uriNotNull = (this.uri != null) ? this.uri : URI.create("/");
        HttpEntity entityCopy = this.entity;
        if (this.parameters != null && !this.parameters.isEmpty()) {
            if (entityCopy == null && ("POST".equalsIgnoreCase(this.method) || "PUT".equalsIgnoreCase(this.method))) {
                entityCopy = new UrlEncodedFormEntity(this.parameters, (this.charset != null) ? this.charset : HTTP.DEF_CONTENT_CHARSET);
            }
            else {
                try {
                    uriNotNull = new URIBuilder(uriNotNull).setCharset(this.charset).addParameters(this.parameters).build();
                }
                catch (URISyntaxException ex) {}
            }
        }
        HttpRequestBase result;
        if (entityCopy == null) {
            result = new InternalRequest(this.method);
        }
        else {
            final InternalEntityEclosingRequest request = new InternalEntityEclosingRequest(this.method);
            request.setEntity(entityCopy);
            result = request;
        }
        result.setProtocolVersion(this.version);
        result.setURI(uriNotNull);
        if (this.headergroup != null) {
            result.setHeaders(this.headergroup.getAllHeaders());
        }
        result.setConfig(this.config);
        return result;
    }
    
    static class InternalRequest extends HttpRequestBase
    {
        private final String method;
        
        InternalRequest(final String method) {
            this.method = method;
        }
        
        @Override
        public String getMethod() {
            return this.method;
        }
    }
    
    static class InternalEntityEclosingRequest extends HttpEntityEnclosingRequestBase
    {
        private final String method;
        
        InternalEntityEclosingRequest(final String method) {
            this.method = method;
        }
        
        @Override
        public String getMethod() {
            return this.method;
        }
    }
}
