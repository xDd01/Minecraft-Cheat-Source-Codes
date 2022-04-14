package org.apache.http.impl.conn;

import org.apache.http.annotation.*;
import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.conn.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultSchemePortResolver implements SchemePortResolver
{
    public static final DefaultSchemePortResolver INSTANCE;
    
    @Override
    public int resolve(final HttpHost host) throws UnsupportedSchemeException {
        Args.notNull(host, "HTTP host");
        final int port = host.getPort();
        if (port > 0) {
            return port;
        }
        final String name = host.getSchemeName();
        if (name.equalsIgnoreCase("http")) {
            return 80;
        }
        if (name.equalsIgnoreCase("https")) {
            return 443;
        }
        throw new UnsupportedSchemeException(name + " protocol is not supported");
    }
    
    static {
        INSTANCE = new DefaultSchemePortResolver();
    }
}
