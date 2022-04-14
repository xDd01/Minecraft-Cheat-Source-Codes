package org.apache.http.impl.execchain;

import org.apache.http.*;
import java.io.*;

class RequestEntityProxy implements HttpEntity
{
    private final HttpEntity original;
    private boolean consumed;
    
    static void enhance(final HttpEntityEnclosingRequest request) {
        final HttpEntity entity = request.getEntity();
        if (entity != null && !entity.isRepeatable() && !isEnhanced(entity)) {
            request.setEntity(new RequestEntityProxy(entity));
        }
    }
    
    static boolean isEnhanced(final HttpEntity entity) {
        return entity instanceof RequestEntityProxy;
    }
    
    static boolean isRepeatable(final HttpRequest request) {
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null) {
                if (isEnhanced(entity)) {
                    final RequestEntityProxy proxy = (RequestEntityProxy)entity;
                    if (!proxy.isConsumed()) {
                        return true;
                    }
                }
                return entity.isRepeatable();
            }
        }
        return true;
    }
    
    RequestEntityProxy(final HttpEntity original) {
        this.consumed = false;
        this.original = original;
    }
    
    public HttpEntity getOriginal() {
        return this.original;
    }
    
    public boolean isConsumed() {
        return this.consumed;
    }
    
    @Override
    public boolean isRepeatable() {
        return this.original.isRepeatable();
    }
    
    @Override
    public boolean isChunked() {
        return this.original.isChunked();
    }
    
    @Override
    public long getContentLength() {
        return this.original.getContentLength();
    }
    
    @Override
    public Header getContentType() {
        return this.original.getContentType();
    }
    
    @Override
    public Header getContentEncoding() {
        return this.original.getContentEncoding();
    }
    
    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return this.original.getContent();
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        this.consumed = true;
        this.original.writeTo(outstream);
    }
    
    @Override
    public boolean isStreaming() {
        return this.original.isStreaming();
    }
    
    @Deprecated
    @Override
    public void consumeContent() throws IOException {
        this.consumed = true;
        this.original.consumeContent();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RequestEntityProxy{");
        sb.append(this.original);
        sb.append('}');
        return sb.toString();
    }
}
