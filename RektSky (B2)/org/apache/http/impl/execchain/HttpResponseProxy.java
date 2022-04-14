package org.apache.http.impl.execchain;

import org.apache.http.client.methods.*;
import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.params.*;

class HttpResponseProxy implements CloseableHttpResponse
{
    private final HttpResponse original;
    private final ConnectionHolder connHolder;
    
    public HttpResponseProxy(final HttpResponse original, final ConnectionHolder connHolder) {
        ResponseEntityProxy.enchance(this.original = original, this.connHolder = connHolder);
    }
    
    @Override
    public void close() throws IOException {
        if (this.connHolder != null) {
            this.connHolder.close();
        }
    }
    
    @Override
    public StatusLine getStatusLine() {
        return this.original.getStatusLine();
    }
    
    @Override
    public void setStatusLine(final StatusLine statusline) {
        this.original.setStatusLine(statusline);
    }
    
    @Override
    public void setStatusLine(final ProtocolVersion ver, final int code) {
        this.original.setStatusLine(ver, code);
    }
    
    @Override
    public void setStatusLine(final ProtocolVersion ver, final int code, final String reason) {
        this.original.setStatusLine(ver, code, reason);
    }
    
    @Override
    public void setStatusCode(final int code) throws IllegalStateException {
        this.original.setStatusCode(code);
    }
    
    @Override
    public void setReasonPhrase(final String reason) throws IllegalStateException {
        this.original.setReasonPhrase(reason);
    }
    
    @Override
    public HttpEntity getEntity() {
        return this.original.getEntity();
    }
    
    @Override
    public void setEntity(final HttpEntity entity) {
        this.original.setEntity(entity);
    }
    
    @Override
    public Locale getLocale() {
        return this.original.getLocale();
    }
    
    @Override
    public void setLocale(final Locale loc) {
        this.original.setLocale(loc);
    }
    
    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.original.getProtocolVersion();
    }
    
    @Override
    public boolean containsHeader(final String name) {
        return this.original.containsHeader(name);
    }
    
    @Override
    public Header[] getHeaders(final String name) {
        return this.original.getHeaders(name);
    }
    
    @Override
    public Header getFirstHeader(final String name) {
        return this.original.getFirstHeader(name);
    }
    
    @Override
    public Header getLastHeader(final String name) {
        return this.original.getLastHeader(name);
    }
    
    @Override
    public Header[] getAllHeaders() {
        return this.original.getAllHeaders();
    }
    
    @Override
    public void addHeader(final Header header) {
        this.original.addHeader(header);
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        this.original.addHeader(name, value);
    }
    
    @Override
    public void setHeader(final Header header) {
        this.original.setHeader(header);
    }
    
    @Override
    public void setHeader(final String name, final String value) {
        this.original.setHeader(name, value);
    }
    
    @Override
    public void setHeaders(final Header[] headers) {
        this.original.setHeaders(headers);
    }
    
    @Override
    public void removeHeader(final Header header) {
        this.original.removeHeader(header);
    }
    
    @Override
    public void removeHeaders(final String name) {
        this.original.removeHeaders(name);
    }
    
    @Override
    public HeaderIterator headerIterator() {
        return this.original.headerIterator();
    }
    
    @Override
    public HeaderIterator headerIterator(final String name) {
        return this.original.headerIterator(name);
    }
    
    @Deprecated
    @Override
    public HttpParams getParams() {
        return this.original.getParams();
    }
    
    @Deprecated
    @Override
    public void setParams(final HttpParams params) {
        this.original.setParams(params);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpResponseProxy{");
        sb.append(this.original);
        sb.append('}');
        return sb.toString();
    }
}
