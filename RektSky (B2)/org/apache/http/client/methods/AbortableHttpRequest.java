package org.apache.http.client.methods;

import java.io.*;
import org.apache.http.conn.*;

@Deprecated
public interface AbortableHttpRequest
{
    void setConnectionRequest(final ClientConnectionRequest p0) throws IOException;
    
    void setReleaseTrigger(final ConnectionReleaseTrigger p0) throws IOException;
    
    void abort();
}
