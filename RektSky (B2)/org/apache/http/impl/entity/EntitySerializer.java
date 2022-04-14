package org.apache.http.impl.entity;

import org.apache.http.annotation.*;
import org.apache.http.entity.*;
import org.apache.http.util.*;
import org.apache.http.io.*;
import org.apache.http.impl.io.*;
import java.io.*;
import org.apache.http.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class EntitySerializer
{
    private final ContentLengthStrategy lenStrategy;
    
    public EntitySerializer(final ContentLengthStrategy lenStrategy) {
        this.lenStrategy = Args.notNull(lenStrategy, "Content length strategy");
    }
    
    protected OutputStream doSerialize(final SessionOutputBuffer outbuffer, final HttpMessage message) throws HttpException, IOException {
        final long len = this.lenStrategy.determineLength(message);
        if (len == -2L) {
            return new ChunkedOutputStream(outbuffer);
        }
        if (len == -1L) {
            return new IdentityOutputStream(outbuffer);
        }
        return new ContentLengthOutputStream(outbuffer, len);
    }
    
    public void serialize(final SessionOutputBuffer outbuffer, final HttpMessage message, final HttpEntity entity) throws HttpException, IOException {
        Args.notNull(outbuffer, "Session output buffer");
        Args.notNull(message, "HTTP message");
        Args.notNull(entity, "HTTP entity");
        final OutputStream outstream = this.doSerialize(outbuffer, message);
        entity.writeTo(outstream);
        outstream.close();
    }
}
