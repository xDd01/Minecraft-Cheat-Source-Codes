package org.apache.http.conn;

import java.net.*;

public interface DnsResolver
{
    InetAddress[] resolve(final String p0) throws UnknownHostException;
}
