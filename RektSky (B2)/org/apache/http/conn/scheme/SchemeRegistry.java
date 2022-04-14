package org.apache.http.conn.scheme;

import org.apache.http.annotation.*;
import java.util.concurrent.*;
import org.apache.http.*;
import org.apache.http.util.*;
import java.util.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public final class SchemeRegistry
{
    private final ConcurrentHashMap<String, Scheme> registeredSchemes;
    
    public SchemeRegistry() {
        this.registeredSchemes = new ConcurrentHashMap<String, Scheme>();
    }
    
    public final Scheme getScheme(final String name) {
        final Scheme found = this.get(name);
        if (found == null) {
            throw new IllegalStateException("Scheme '" + name + "' not registered.");
        }
        return found;
    }
    
    public final Scheme getScheme(final HttpHost host) {
        Args.notNull(host, "Host");
        return this.getScheme(host.getSchemeName());
    }
    
    public final Scheme get(final String name) {
        Args.notNull(name, "Scheme name");
        final Scheme found = this.registeredSchemes.get(name);
        return found;
    }
    
    public final Scheme register(final Scheme sch) {
        Args.notNull(sch, "Scheme");
        final Scheme old = this.registeredSchemes.put(sch.getName(), sch);
        return old;
    }
    
    public final Scheme unregister(final String name) {
        Args.notNull(name, "Scheme name");
        final Scheme gone = this.registeredSchemes.remove(name);
        return gone;
    }
    
    public final List<String> getSchemeNames() {
        return new ArrayList<String>(this.registeredSchemes.keySet());
    }
    
    public void setItems(final Map<String, Scheme> map) {
        if (map == null) {
            return;
        }
        this.registeredSchemes.clear();
        this.registeredSchemes.putAll(map);
    }
}
