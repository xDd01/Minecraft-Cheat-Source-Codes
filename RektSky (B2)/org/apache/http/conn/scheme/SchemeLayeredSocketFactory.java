package org.apache.http.conn.scheme;

import org.apache.http.params.*;
import java.io.*;
import java.net.*;

@Deprecated
public interface SchemeLayeredSocketFactory extends SchemeSocketFactory
{
    Socket createLayeredSocket(final Socket p0, final String p1, final int p2, final HttpParams p3) throws IOException, UnknownHostException;
}
