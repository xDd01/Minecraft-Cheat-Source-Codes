package org.apache.http.conn;

import java.io.*;

public interface EofSensorWatcher
{
    boolean eofDetected(final InputStream p0) throws IOException;
    
    boolean streamClosed(final InputStream p0) throws IOException;
    
    boolean streamAbort(final InputStream p0) throws IOException;
}
