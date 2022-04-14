package org.apache.http.conn.ssl;

import java.io.*;
import java.security.cert.*;
import javax.net.ssl.*;

@Deprecated
public interface X509HostnameVerifier extends HostnameVerifier
{
    void verify(final String p0, final SSLSocket p1) throws IOException;
    
    void verify(final String p0, final X509Certificate p1) throws SSLException;
    
    void verify(final String p0, final String[] p1, final String[] p2) throws SSLException;
}
