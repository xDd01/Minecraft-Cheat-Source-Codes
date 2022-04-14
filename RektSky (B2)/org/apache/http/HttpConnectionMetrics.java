package org.apache.http;

public interface HttpConnectionMetrics
{
    long getRequestCount();
    
    long getResponseCount();
    
    long getSentBytesCount();
    
    long getReceivedBytesCount();
    
    Object getMetric(final String p0);
    
    void reset();
}
