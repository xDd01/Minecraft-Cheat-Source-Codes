package org.apache.http.conn.params;

import org.apache.http.annotation.*;
import java.util.concurrent.*;
import org.apache.http.conn.routing.*;
import org.apache.http.util.*;
import java.util.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public final class ConnPerRouteBean implements ConnPerRoute
{
    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
    private final ConcurrentHashMap<HttpRoute, Integer> maxPerHostMap;
    private volatile int defaultMax;
    
    public ConnPerRouteBean(final int defaultMax) {
        this.maxPerHostMap = new ConcurrentHashMap<HttpRoute, Integer>();
        this.setDefaultMaxPerRoute(defaultMax);
    }
    
    public ConnPerRouteBean() {
        this(2);
    }
    
    public int getDefaultMax() {
        return this.defaultMax;
    }
    
    public int getDefaultMaxPerRoute() {
        return this.defaultMax;
    }
    
    public void setDefaultMaxPerRoute(final int max) {
        Args.positive(max, "Default max per route");
        this.defaultMax = max;
    }
    
    public void setMaxForRoute(final HttpRoute route, final int max) {
        Args.notNull(route, "HTTP route");
        Args.positive(max, "Max per route");
        this.maxPerHostMap.put(route, max);
    }
    
    @Override
    public int getMaxForRoute(final HttpRoute route) {
        Args.notNull(route, "HTTP route");
        final Integer max = this.maxPerHostMap.get(route);
        if (max != null) {
            return max;
        }
        return this.defaultMax;
    }
    
    public void setMaxForRoutes(final Map<HttpRoute, Integer> map) {
        if (map == null) {
            return;
        }
        this.maxPerHostMap.clear();
        this.maxPerHostMap.putAll(map);
    }
    
    @Override
    public String toString() {
        return this.maxPerHostMap.toString();
    }
}
