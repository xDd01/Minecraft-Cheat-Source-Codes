package org.apache.logging.log4j.core.helpers;

import org.apache.logging.log4j.*;
import java.net.*;
import java.util.*;
import org.apache.logging.log4j.status.*;

public final class NetUtils
{
    private static final Logger LOGGER;
    
    private NetUtils() {
    }
    
    public static String getLocalHostname() {
        try {
            final InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        }
        catch (UnknownHostException uhe) {
            try {
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    final NetworkInterface nic = interfaces.nextElement();
                    final Enumeration<InetAddress> addresses = nic.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        final InetAddress address = addresses.nextElement();
                        if (!address.isLoopbackAddress()) {
                            final String hostname = address.getHostName();
                            if (hostname != null) {
                                return hostname;
                            }
                            continue;
                        }
                    }
                }
            }
            catch (SocketException se) {
                NetUtils.LOGGER.error("Could not determine local host name", uhe);
                return "UNKNOWN_LOCALHOST";
            }
            NetUtils.LOGGER.error("Could not determine local host name", uhe);
            return "UNKNOWN_LOCALHOST";
        }
    }
    
    static {
        LOGGER = StatusLogger.getLogger();
    }
}
