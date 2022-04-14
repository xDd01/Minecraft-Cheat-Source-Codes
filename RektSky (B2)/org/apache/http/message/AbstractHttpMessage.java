package org.apache.http.message;

import org.apache.http.util.*;
import org.apache.http.*;
import org.apache.http.params.*;

public abstract class AbstractHttpMessage implements HttpMessage
{
    protected HeaderGroup headergroup;
    @Deprecated
    protected HttpParams params;
    
    @Deprecated
    protected AbstractHttpMessage(final HttpParams params) {
        this.headergroup = new HeaderGroup();
        this.params = params;
    }
    
    protected AbstractHttpMessage() {
        this(null);
    }
    
    @Override
    public boolean containsHeader(final String name) {
        return this.headergroup.containsHeader(name);
    }
    
    @Override
    public Header[] getHeaders(final String name) {
        return this.headergroup.getHeaders(name);
    }
    
    @Override
    public Header getFirstHeader(final String name) {
        return this.headergroup.getFirstHeader(name);
    }
    
    @Override
    public Header getLastHeader(final String name) {
        return this.headergroup.getLastHeader(name);
    }
    
    @Override
    public Header[] getAllHeaders() {
        return this.headergroup.getAllHeaders();
    }
    
    @Override
    public void addHeader(final Header header) {
        this.headergroup.addHeader(header);
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        Args.notNull(name, "Header name");
        this.headergroup.addHeader(new BasicHeader(name, value));
    }
    
    @Override
    public void setHeader(final Header header) {
        this.headergroup.updateHeader(header);
    }
    
    @Override
    public void setHeader(final String name, final String value) {
        Args.notNull(name, "Header name");
        this.headergroup.updateHeader(new BasicHeader(name, value));
    }
    
    @Override
    public void setHeaders(final Header[] headers) {
        this.headergroup.setHeaders(headers);
    }
    
    @Override
    public void removeHeader(final Header header) {
        this.headergroup.removeHeader(header);
    }
    
    @Override
    public void removeHeaders(final String name) {
        if (name == null) {
            return;
        }
        final HeaderIterator i = this.headergroup.iterator();
        while (i.hasNext()) {
            final Header header = i.nextHeader();
            if (name.equalsIgnoreCase(header.getName())) {
                i.remove();
            }
        }
    }
    
    @Override
    public HeaderIterator headerIterator() {
        return this.headergroup.iterator();
    }
    
    @Override
    public HeaderIterator headerIterator(final String name) {
        return this.headergroup.iterator(name);
    }
    
    @Deprecated
    @Override
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = new BasicHttpParams();
        }
        return this.params;
    }
    
    @Deprecated
    @Override
    public void setParams(final HttpParams params) {
        this.params = Args.notNull(params, "HTTP parameters");
    }
}
