package org.apache.http.impl.client;

import org.apache.http.client.*;
import org.apache.http.annotation.*;
import java.util.concurrent.*;
import org.apache.http.auth.*;
import org.apache.http.util.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.SAFE)
public class BasicCredentialsProvider implements CredentialsProvider
{
    private final ConcurrentHashMap<AuthScope, Credentials> credMap;
    
    public BasicCredentialsProvider() {
        this.credMap = new ConcurrentHashMap<AuthScope, Credentials>();
    }
    
    @Override
    public void setCredentials(final AuthScope authscope, final Credentials credentials) {
        Args.notNull(authscope, "Authentication scope");
        this.credMap.put(authscope, credentials);
    }
    
    private static Credentials matchCredentials(final Map<AuthScope, Credentials> map, final AuthScope authscope) {
        Credentials creds = map.get(authscope);
        if (creds == null) {
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (final AuthScope current : map.keySet()) {
                final int factor = authscope.match(current);
                if (factor > bestMatchFactor) {
                    bestMatchFactor = factor;
                    bestMatch = current;
                }
            }
            if (bestMatch != null) {
                creds = map.get(bestMatch);
            }
        }
        return creds;
    }
    
    @Override
    public Credentials getCredentials(final AuthScope authscope) {
        Args.notNull(authscope, "Authentication scope");
        return matchCredentials(this.credMap, authscope);
    }
    
    @Override
    public void clear() {
        this.credMap.clear();
    }
    
    @Override
    public String toString() {
        return this.credMap.toString();
    }
}
