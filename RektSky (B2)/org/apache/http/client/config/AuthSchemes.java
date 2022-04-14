package org.apache.http.client.config;

public final class AuthSchemes
{
    public static final String BASIC = "Basic";
    public static final String DIGEST = "Digest";
    public static final String NTLM = "NTLM";
    public static final String SPNEGO = "Negotiate";
    public static final String KERBEROS = "Kerberos";
    public static final String CREDSSP = "CredSSP";
    
    private AuthSchemes() {
    }
}
