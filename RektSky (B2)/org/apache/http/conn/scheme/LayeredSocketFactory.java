package org.apache.http.conn.scheme;

import java.io.*;
import java.net.*;

@Deprecated
public interface LayeredSocketFactory extends SocketFactory
{
    Socket createSocket(final Socket p0, final String p1, final int p2, final boolean p3) throws IOException, UnknownHostException;
}
