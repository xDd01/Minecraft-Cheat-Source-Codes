package org.apache.http;

import java.io.*;

public interface HttpClientConnection extends HttpConnection
{
    boolean isResponseAvailable(final int p0) throws IOException;
    
    void sendRequestHeader(final HttpRequest p0) throws HttpException, IOException;
    
    void sendRequestEntity(final HttpEntityEnclosingRequest p0) throws HttpException, IOException;
    
    HttpResponse receiveResponseHeader() throws HttpException, IOException;
    
    void receiveResponseEntity(final HttpResponse p0) throws HttpException, IOException;
    
    void flush() throws IOException;
}
