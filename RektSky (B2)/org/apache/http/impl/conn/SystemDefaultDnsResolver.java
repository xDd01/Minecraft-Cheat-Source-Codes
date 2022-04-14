package org.apache.http.impl.conn;

import org.apache.http.conn.*;
import java.net.*;

public class SystemDefaultDnsResolver implements DnsResolver
{
    public static final SystemDefaultDnsResolver INSTANCE;
    
    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        return InetAddress.getAllByName(host);
    }
    
    static {
        INSTANCE = new SystemDefaultDnsResolver();
    }
}
