package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.http.*;
import org.apache.http.auth.*;
import java.util.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultTargetAuthenticationHandler extends AbstractAuthenticationHandler
{
    @Override
    public boolean isAuthenticationRequested(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final int status = response.getStatusLine().getStatusCode();
        return status == 401;
    }
    
    @Override
    public Map<String, Header> getChallenges(final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        Args.notNull(response, "HTTP response");
        final Header[] headers = response.getHeaders("WWW-Authenticate");
        return this.parseChallenges(headers);
    }
    
    @Override
    protected List<String> getAuthPreferences(final HttpResponse response, final HttpContext context) {
        final List<String> authpref = (List<String>)response.getParams().getParameter("http.auth.target-scheme-pref");
        if (authpref != null) {
            return authpref;
        }
        return super.getAuthPreferences(response, context);
    }
}
