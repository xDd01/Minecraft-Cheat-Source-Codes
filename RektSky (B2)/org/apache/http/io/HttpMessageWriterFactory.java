package org.apache.http.io;

import org.apache.http.*;

public interface HttpMessageWriterFactory<T extends HttpMessage>
{
    HttpMessageWriter<T> create(final SessionOutputBuffer p0);
}
