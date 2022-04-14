package org.apache.http.client.entity;

import org.apache.http.entity.*;
import org.apache.http.*;
import org.apache.http.message.*;
import java.io.*;
import org.apache.http.util.*;
import java.util.zip.*;

public class GzipCompressingEntity extends HttpEntityWrapper
{
    private static final String GZIP_CODEC = "gzip";
    
    public GzipCompressingEntity(final HttpEntity entity) {
        super(entity);
    }
    
    @Override
    public Header getContentEncoding() {
        return new BasicHeader("Content-Encoding", "gzip");
    }
    
    @Override
    public long getContentLength() {
        return -1L;
    }
    
    @Override
    public boolean isChunked() {
        return true;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        final GZIPOutputStream gzip = new GZIPOutputStream(outstream);
        this.wrappedEntity.writeTo(gzip);
        gzip.close();
    }
}
