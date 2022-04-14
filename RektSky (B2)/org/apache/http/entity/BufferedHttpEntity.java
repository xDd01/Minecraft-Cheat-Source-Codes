package org.apache.http.entity;

import org.apache.http.*;
import java.io.*;
import org.apache.http.util.*;

public class BufferedHttpEntity extends HttpEntityWrapper
{
    private final byte[] buffer;
    
    public BufferedHttpEntity(final HttpEntity entity) throws IOException {
        super(entity);
        if (!entity.isRepeatable() || entity.getContentLength() < 0L) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            entity.writeTo(out);
            out.flush();
            this.buffer = out.toByteArray();
        }
        else {
            this.buffer = null;
        }
    }
    
    @Override
    public long getContentLength() {
        if (this.buffer != null) {
            return this.buffer.length;
        }
        return super.getContentLength();
    }
    
    @Override
    public InputStream getContent() throws IOException {
        if (this.buffer != null) {
            return new ByteArrayInputStream(this.buffer);
        }
        return super.getContent();
    }
    
    @Override
    public boolean isChunked() {
        return this.buffer == null && super.isChunked();
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        if (this.buffer != null) {
            outstream.write(this.buffer);
        }
        else {
            super.writeTo(outstream);
        }
    }
    
    @Override
    public boolean isStreaming() {
        return this.buffer == null && super.isStreaming();
    }
}
