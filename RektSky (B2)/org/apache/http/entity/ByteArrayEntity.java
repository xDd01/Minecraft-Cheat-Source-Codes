package org.apache.http.entity;

import org.apache.http.util.*;
import java.io.*;

public class ByteArrayEntity extends AbstractHttpEntity implements Cloneable
{
    @Deprecated
    protected final byte[] content;
    private final byte[] b;
    private final int off;
    private final int len;
    
    public ByteArrayEntity(final byte[] b, final ContentType contentType) {
        Args.notNull(b, "Source byte array");
        this.content = b;
        this.b = b;
        this.off = 0;
        this.len = this.b.length;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    public ByteArrayEntity(final byte[] b, final int off, final int len, final ContentType contentType) {
        Args.notNull(b, "Source byte array");
        if (off < 0 || off > b.length || len < 0 || off + len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
        }
        this.content = b;
        this.b = b;
        this.off = off;
        this.len = len;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    public ByteArrayEntity(final byte[] b) {
        this(b, null);
    }
    
    public ByteArrayEntity(final byte[] b, final int off, final int len) {
        this(b, off, len, null);
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public long getContentLength() {
        return this.len;
    }
    
    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(this.b, this.off, this.len);
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        outstream.write(this.b, this.off, this.len);
        outstream.flush();
    }
    
    @Override
    public boolean isStreaming() {
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
