package org.apache.http.impl.pool;

import org.apache.http.pool.*;
import org.apache.http.*;
import org.apache.http.annotation.*;
import java.io.*;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicPoolEntry extends PoolEntry<HttpHost, HttpClientConnection>
{
    public BasicPoolEntry(final String id, final HttpHost route, final HttpClientConnection conn) {
        super(id, route, conn);
    }
    
    @Override
    public void close() {
        try {
            ((PoolEntry<T, HttpClientConnection>)this).getConnection().close();
        }
        catch (IOException ex) {}
    }
    
    @Override
    public boolean isClosed() {
        return !((PoolEntry<T, HttpClientConnection>)this).getConnection().isOpen();
    }
}
