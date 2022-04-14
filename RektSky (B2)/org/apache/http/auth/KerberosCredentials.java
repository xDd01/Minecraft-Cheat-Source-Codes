package org.apache.http.auth;

import java.io.*;
import org.apache.http.annotation.*;
import org.ietf.jgss.*;
import java.security.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class KerberosCredentials implements Credentials, Serializable
{
    private static final long serialVersionUID = 487421613855550713L;
    private final GSSCredential gssCredential;
    
    public KerberosCredentials(final GSSCredential gssCredential) {
        this.gssCredential = gssCredential;
    }
    
    public GSSCredential getGSSCredential() {
        return this.gssCredential;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return null;
    }
    
    @Override
    public String getPassword() {
        return null;
    }
}
