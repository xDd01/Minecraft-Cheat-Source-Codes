package org.apache.http;

import java.net.*;
import java.io.*;

public interface HttpConnectionFactory<T extends HttpConnection>
{
    T createConnection(final Socket p0) throws IOException;
}
