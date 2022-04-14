package org.apache.http;

import java.io.*;

public interface HttpConnection extends Closeable
{
    void close() throws IOException;
    
    boolean isOpen();
    
    boolean isStale();
    
    void setSocketTimeout(final int p0);
    
    int getSocketTimeout();
    
    void shutdown() throws IOException;
    
    HttpConnectionMetrics getMetrics();
}
