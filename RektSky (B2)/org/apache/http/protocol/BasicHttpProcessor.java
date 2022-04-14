package org.apache.http.protocol;

import org.apache.http.util.*;
import java.io.*;
import org.apache.http.*;
import java.util.*;

@Deprecated
public final class BasicHttpProcessor implements HttpProcessor, HttpRequestInterceptorList, HttpResponseInterceptorList, Cloneable
{
    protected final List<HttpRequestInterceptor> requestInterceptors;
    protected final List<HttpResponseInterceptor> responseInterceptors;
    
    public BasicHttpProcessor() {
        this.requestInterceptors = new ArrayList<HttpRequestInterceptor>();
        this.responseInterceptors = new ArrayList<HttpResponseInterceptor>();
    }
    
    @Override
    public void addRequestInterceptor(final HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return;
        }
        this.requestInterceptors.add(itcp);
    }
    
    @Override
    public void addRequestInterceptor(final HttpRequestInterceptor itcp, final int index) {
        if (itcp == null) {
            return;
        }
        this.requestInterceptors.add(index, itcp);
    }
    
    @Override
    public void addResponseInterceptor(final HttpResponseInterceptor itcp, final int index) {
        if (itcp == null) {
            return;
        }
        this.responseInterceptors.add(index, itcp);
    }
    
    @Override
    public void removeRequestInterceptorByClass(final Class<? extends HttpRequestInterceptor> clazz) {
        final Iterator<HttpRequestInterceptor> it = this.requestInterceptors.iterator();
        while (it.hasNext()) {
            final Object request = it.next();
            if (request.getClass().equals(clazz)) {
                it.remove();
            }
        }
    }
    
    @Override
    public void removeResponseInterceptorByClass(final Class<? extends HttpResponseInterceptor> clazz) {
        final Iterator<HttpResponseInterceptor> it = this.responseInterceptors.iterator();
        while (it.hasNext()) {
            final Object request = it.next();
            if (request.getClass().equals(clazz)) {
                it.remove();
            }
        }
    }
    
    public final void addInterceptor(final HttpRequestInterceptor interceptor) {
        this.addRequestInterceptor(interceptor);
    }
    
    public final void addInterceptor(final HttpRequestInterceptor interceptor, final int index) {
        this.addRequestInterceptor(interceptor, index);
    }
    
    @Override
    public int getRequestInterceptorCount() {
        return this.requestInterceptors.size();
    }
    
    @Override
    public HttpRequestInterceptor getRequestInterceptor(final int index) {
        if (index < 0 || index >= this.requestInterceptors.size()) {
            return null;
        }
        return this.requestInterceptors.get(index);
    }
    
    @Override
    public void clearRequestInterceptors() {
        this.requestInterceptors.clear();
    }
    
    @Override
    public void addResponseInterceptor(final HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return;
        }
        this.responseInterceptors.add(itcp);
    }
    
    public final void addInterceptor(final HttpResponseInterceptor interceptor) {
        this.addResponseInterceptor(interceptor);
    }
    
    public final void addInterceptor(final HttpResponseInterceptor interceptor, final int index) {
        this.addResponseInterceptor(interceptor, index);
    }
    
    @Override
    public int getResponseInterceptorCount() {
        return this.responseInterceptors.size();
    }
    
    @Override
    public HttpResponseInterceptor getResponseInterceptor(final int index) {
        if (index < 0 || index >= this.responseInterceptors.size()) {
            return null;
        }
        return this.responseInterceptors.get(index);
    }
    
    @Override
    public void clearResponseInterceptors() {
        this.responseInterceptors.clear();
    }
    
    @Override
    public void setInterceptors(final List<?> list) {
        Args.notNull(list, "Inteceptor list");
        this.requestInterceptors.clear();
        this.responseInterceptors.clear();
        for (final Object obj : list) {
            if (obj instanceof HttpRequestInterceptor) {
                this.addInterceptor((HttpRequestInterceptor)obj);
            }
            if (obj instanceof HttpResponseInterceptor) {
                this.addInterceptor((HttpResponseInterceptor)obj);
            }
        }
    }
    
    public void clearInterceptors() {
        this.clearRequestInterceptors();
        this.clearResponseInterceptors();
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws IOException, HttpException {
        for (final HttpRequestInterceptor interceptor : this.requestInterceptors) {
            interceptor.process(request, context);
        }
    }
    
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws IOException, HttpException {
        for (final HttpResponseInterceptor interceptor : this.responseInterceptors) {
            interceptor.process(response, context);
        }
    }
    
    protected void copyInterceptors(final BasicHttpProcessor target) {
        target.requestInterceptors.clear();
        target.requestInterceptors.addAll(this.requestInterceptors);
        target.responseInterceptors.clear();
        target.responseInterceptors.addAll(this.responseInterceptors);
    }
    
    public BasicHttpProcessor copy() {
        final BasicHttpProcessor clone = new BasicHttpProcessor();
        this.copyInterceptors(clone);
        return clone;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final BasicHttpProcessor clone = (BasicHttpProcessor)super.clone();
        this.copyInterceptors(clone);
        return clone;
    }
}
