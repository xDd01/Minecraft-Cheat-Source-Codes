package org.apache.http.auth;

import java.security.*;

public interface Credentials
{
    Principal getUserPrincipal();
    
    String getPassword();
}
