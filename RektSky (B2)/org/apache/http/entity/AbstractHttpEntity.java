package org.apache.http.entity;

import org.apache.http.*;
import org.apache.http.message.*;
import java.io.*;

public abstract class AbstractHttpEntity implements HttpEntity
{
    protected static final int OUTPUT_BUFFER_SIZE = 4096;
    protected Header contentType;
    protected Header contentEncoding;
    protected boolean chunked;
    
    protected AbstractHttpEntity() {
    }
    
    @Override
    public Header getContentType() {
        return this.contentType;
    }
    
    @Override
    public Header getContentEncoding() {
        return this.contentEncoding;
    }
    
    @Override
    public boolean isChunked() {
        return this.chunked;
    }
    
    public void setContentType(final Header contentType) {
        this.contentType = contentType;
    }
    
    public void setContentType(final String ctString) {
        Header h = null;
        if (ctString != null) {
            h = new BasicHeader("Content-Type", ctString);
        }
        this.setContentType(h);
    }
    
    public void setContentEncoding(final Header contentEncoding) {
        this.contentEncoding = contentEncoding;
    }
    
    public void setContentEncoding(final String ceString) {
        Header h = null;
        if (ceString != null) {
            h = new BasicHeader("Content-Encoding", ceString);
        }
        this.setContentEncoding(h);
    }
    
    public void setChunked(final boolean b) {
        this.chunked = b;
    }
    
    @Deprecated
    @Override
    public void consumeContent() throws IOException {
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (this.contentType != null) {
            sb.append("Content-Type: ");
            sb.append(this.contentType.getValue());
            sb.append(',');
        }
        if (this.contentEncoding != null) {
            sb.append("Content-Encoding: ");
            sb.append(this.contentEncoding.getValue());
            sb.append(',');
        }
        final long len = this.getContentLength();
        if (len >= 0L) {
            sb.append("Content-Length: ");
            sb.append(len);
            sb.append(',');
        }
        sb.append("Chunked: ");
        sb.append(this.chunked);
        sb.append(']');
        return sb.toString();
    }
}
