package org.apache.http.impl.auth;

import org.apache.http.protocol.*;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.ietf.jgss.*;
import org.apache.http.util.*;

public class KerberosScheme extends GGSSchemeBase
{
    private static final String KERBEROS_OID = "1.2.840.113554.1.2.2";
    
    public KerberosScheme(final boolean stripPort, final boolean useCanonicalHostname) {
        super(stripPort, useCanonicalHostname);
    }
    
    public KerberosScheme(final boolean stripPort) {
        super(stripPort);
    }
    
    public KerberosScheme() {
    }
    
    @Override
    public String getSchemeName() {
        return "Kerberos";
    }
    
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        return super.authenticate(credentials, request, context);
    }
    
    @Override
    protected byte[] generateToken(final byte[] input, final String authServer) throws GSSException {
        return super.generateToken(input, authServer);
    }
    
    @Override
    protected byte[] generateToken(final byte[] input, final String authServer, final Credentials credentials) throws GSSException {
        return this.generateGSSToken(input, new Oid("1.2.840.113554.1.2.2"), authServer, credentials);
    }
    
    @Override
    public String getParameter(final String name) {
        Args.notNull(name, "Parameter name");
        return null;
    }
    
    @Override
    public String getRealm() {
        return null;
    }
    
    @Override
    public boolean isConnectionBased() {
        return true;
    }
}
