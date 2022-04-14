package org.apache.http.io;

import java.io.*;
import org.apache.http.util.*;

public interface SessionInputBuffer
{
    int read(final byte[] p0, final int p1, final int p2) throws IOException;
    
    int read(final byte[] p0) throws IOException;
    
    int read() throws IOException;
    
    int readLine(final CharArrayBuffer p0) throws IOException;
    
    String readLine() throws IOException;
    
    @Deprecated
    boolean isDataAvailable(final int p0) throws IOException;
    
    HttpTransportMetrics getMetrics();
}
