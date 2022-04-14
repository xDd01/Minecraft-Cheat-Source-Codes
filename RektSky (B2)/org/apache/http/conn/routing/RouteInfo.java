package org.apache.http.conn.routing;

import org.apache.http.*;
import java.net.*;

public interface RouteInfo
{
    HttpHost getTargetHost();
    
    InetAddress getLocalAddress();
    
    int getHopCount();
    
    HttpHost getHopTarget(final int p0);
    
    HttpHost getProxyHost();
    
    TunnelType getTunnelType();
    
    boolean isTunnelled();
    
    LayerType getLayerType();
    
    boolean isLayered();
    
    boolean isSecure();
    
    public enum TunnelType
    {
        PLAIN, 
        TUNNELLED;
    }
    
    public enum LayerType
    {
        PLAIN, 
        LAYERED;
    }
}
