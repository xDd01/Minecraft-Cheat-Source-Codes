package org.apache.http.impl.bootstrap;

import javax.net.ssl.*;

public interface SSLServerSetupHandler
{
    void initialize(final SSLServerSocket p0) throws SSLException;
}
