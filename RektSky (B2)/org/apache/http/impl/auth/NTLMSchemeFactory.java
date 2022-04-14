package org.apache.http.impl.auth;

import org.apache.http.annotation.*;
import org.apache.http.params.*;
import org.apache.http.auth.*;
import org.apache.http.protocol.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NTLMSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    @Override
    public AuthScheme newInstance(final HttpParams params) {
        return new NTLMScheme();
    }
    
    @Override
    public AuthScheme create(final HttpContext context) {
        return new NTLMScheme();
    }
}
