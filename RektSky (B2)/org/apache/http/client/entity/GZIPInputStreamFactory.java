package org.apache.http.client.entity;

import org.apache.http.annotation.*;
import java.util.zip.*;
import java.io.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class GZIPInputStreamFactory implements InputStreamFactory
{
    private static final GZIPInputStreamFactory INSTANCE;
    
    public static GZIPInputStreamFactory getInstance() {
        return GZIPInputStreamFactory.INSTANCE;
    }
    
    @Override
    public InputStream create(final InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
    }
    
    static {
        INSTANCE = new GZIPInputStreamFactory();
    }
}
