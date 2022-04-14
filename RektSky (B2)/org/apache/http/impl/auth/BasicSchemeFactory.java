package org.apache.http.impl.auth;

import org.apache.http.annotation.*;
import java.nio.charset.*;
import org.apache.http.params.*;
import org.apache.http.auth.*;
import org.apache.http.protocol.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    private final Charset charset;
    
    public BasicSchemeFactory(final Charset charset) {
        this.charset = charset;
    }
    
    public BasicSchemeFactory() {
        this(null);
    }
    
    @Override
    public AuthScheme newInstance(final HttpParams params) {
        return new BasicScheme();
    }
    
    @Override
    public AuthScheme create(final HttpContext context) {
        return new BasicScheme(this.charset);
    }
}
