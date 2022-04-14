package org.apache.http.entity;

import org.apache.http.util.*;
import java.io.*;

public class EntityTemplate extends AbstractHttpEntity
{
    private final ContentProducer contentproducer;
    
    public EntityTemplate(final ContentProducer contentproducer) {
        this.contentproducer = Args.notNull(contentproducer, "Content producer");
    }
    
    @Override
    public long getContentLength() {
        return -1L;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        this.writeTo(buf);
        return new ByteArrayInputStream(buf.toByteArray());
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        this.contentproducer.writeTo(outstream);
    }
    
    @Override
    public boolean isStreaming() {
        return false;
    }
}
