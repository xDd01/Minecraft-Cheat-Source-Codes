package org.apache.http.entity;

import org.apache.http.util.*;
import java.io.*;

public class InputStreamEntity extends AbstractHttpEntity
{
    private final InputStream content;
    private final long length;
    
    public InputStreamEntity(final InputStream instream) {
        this(instream, -1L);
    }
    
    public InputStreamEntity(final InputStream instream, final long length) {
        this(instream, length, null);
    }
    
    public InputStreamEntity(final InputStream instream, final ContentType contentType) {
        this(instream, -1L, contentType);
    }
    
    public InputStreamEntity(final InputStream instream, final long length, final ContentType contentType) {
        this.content = Args.notNull(instream, "Source input stream");
        this.length = length;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    @Override
    public boolean isRepeatable() {
        return false;
    }
    
    @Override
    public long getContentLength() {
        return this.length;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return this.content;
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        final InputStream instream = this.content;
        try {
            final byte[] buffer = new byte[4096];
            if (this.length < 0L) {
                int l;
                while ((l = instream.read(buffer)) != -1) {
                    outstream.write(buffer, 0, l);
                }
            }
            else {
                int l;
                for (long remaining = this.length; remaining > 0L; remaining -= l) {
                    l = instream.read(buffer, 0, (int)Math.min(4096L, remaining));
                    if (l == -1) {
                        break;
                    }
                    outstream.write(buffer, 0, l);
                }
            }
        }
        finally {
            instream.close();
        }
    }
    
    @Override
    public boolean isStreaming() {
        return true;
    }
}
