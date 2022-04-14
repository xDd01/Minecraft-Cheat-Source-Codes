package org.apache.http.io;

import java.io.*;
import org.apache.http.*;

public interface HttpMessageWriter<T extends HttpMessage>
{
    void write(final T p0) throws IOException, HttpException;
}
