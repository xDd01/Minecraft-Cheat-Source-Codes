package org.apache.http.client.entity;

import org.apache.http.annotation.*;
import java.io.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DeflateInputStreamFactory implements InputStreamFactory
{
    private static final DeflateInputStreamFactory INSTANCE;
    
    public static DeflateInputStreamFactory getInstance() {
        return DeflateInputStreamFactory.INSTANCE;
    }
    
    @Override
    public InputStream create(final InputStream inputStream) throws IOException {
        return new DeflateInputStream(inputStream);
    }
    
    static {
        INSTANCE = new DeflateInputStreamFactory();
    }
}
