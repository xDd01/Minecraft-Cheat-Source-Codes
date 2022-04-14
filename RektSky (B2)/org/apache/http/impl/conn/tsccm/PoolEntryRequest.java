package org.apache.http.impl.conn.tsccm;

import java.util.concurrent.*;
import org.apache.http.conn.*;

@Deprecated
public interface PoolEntryRequest
{
    BasicPoolEntry getPoolEntry(final long p0, final TimeUnit p1) throws InterruptedException, ConnectionPoolTimeoutException;
    
    void abortRequest();
}
