package org.apache.http.conn.scheme;

import java.net.*;
import java.io.*;

@Deprecated
public interface HostNameResolver
{
    InetAddress resolve(final String p0) throws IOException;
}
