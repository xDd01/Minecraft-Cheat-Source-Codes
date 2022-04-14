package org.apache.http.auth;

import org.apache.http.*;

public interface AuthScheme
{
    void processChallenge(final Header p0) throws MalformedChallengeException;
    
    String getSchemeName();
    
    String getParameter(final String p0);
    
    String getRealm();
    
    boolean isConnectionBased();
    
    boolean isComplete();
    
    @Deprecated
    Header authenticate(final Credentials p0, final HttpRequest p1) throws AuthenticationException;
}
