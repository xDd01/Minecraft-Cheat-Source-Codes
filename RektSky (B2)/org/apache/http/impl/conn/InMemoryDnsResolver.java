package org.apache.http.impl.conn;

import org.apache.http.conn.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import org.apache.http.util.*;
import java.util.*;
import java.net.*;

public class InMemoryDnsResolver implements DnsResolver
{
    private final Log log;
    private final Map<String, InetAddress[]> dnsMap;
    
    public InMemoryDnsResolver() {
        this.log = LogFactory.getLog(InMemoryDnsResolver.class);
        this.dnsMap = new ConcurrentHashMap<String, InetAddress[]>();
    }
    
    public void add(final String host, final InetAddress... ips) {
        Args.notNull(host, "Host name");
        Args.notNull(ips, "Array of IP addresses");
        this.dnsMap.put(host, ips);
    }
    
    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        final InetAddress[] resolvedAddresses = this.dnsMap.get(host);
        if (this.log.isInfoEnabled()) {
            this.log.info("Resolving " + host + " to " + Arrays.deepToString(resolvedAddresses));
        }
        if (resolvedAddresses == null) {
            throw new UnknownHostException(host + " cannot be resolved");
        }
        return resolvedAddresses;
    }
}
