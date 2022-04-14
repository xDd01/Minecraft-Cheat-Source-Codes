package org.apache.http.conn.socket;

import org.apache.http.protocol.*;
import java.io.*;
import java.net.*;

public interface LayeredConnectionSocketFactory extends ConnectionSocketFactory
{
    Socket createLayeredSocket(final Socket p0, final String p1, final int p2, final HttpContext p3) throws IOException, UnknownHostException;
}
