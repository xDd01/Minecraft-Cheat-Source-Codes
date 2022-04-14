package org.apache.http.client;

import org.apache.http.auth.*;

public interface CredentialsProvider
{
    void setCredentials(final AuthScope p0, final Credentials p1);
    
    Credentials getCredentials(final AuthScope p0);
    
    void clear();
}
