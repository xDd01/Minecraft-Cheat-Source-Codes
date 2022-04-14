package org.apache.http.impl.auth;

import org.apache.http.params.*;
import org.apache.http.auth.*;

@Deprecated
public class NegotiateSchemeFactory implements AuthSchemeFactory
{
    private final SpnegoTokenGenerator spengoGenerator;
    private final boolean stripPort;
    
    public NegotiateSchemeFactory(final SpnegoTokenGenerator spengoGenerator, final boolean stripPort) {
        this.spengoGenerator = spengoGenerator;
        this.stripPort = stripPort;
    }
    
    public NegotiateSchemeFactory(final SpnegoTokenGenerator spengoGenerator) {
        this(spengoGenerator, false);
    }
    
    public NegotiateSchemeFactory() {
        this(null, false);
    }
    
    @Override
    public AuthScheme newInstance(final HttpParams params) {
        return new NegotiateScheme(this.spengoGenerator, this.stripPort);
    }
    
    public boolean isStripPort() {
        return this.stripPort;
    }
    
    public SpnegoTokenGenerator getSpengoGenerator() {
        return this.spengoGenerator;
    }
}
