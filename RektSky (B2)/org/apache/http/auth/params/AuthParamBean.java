package org.apache.http.auth.params;

import org.apache.http.params.*;

@Deprecated
public class AuthParamBean extends HttpAbstractParamBean
{
    public AuthParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setCredentialCharset(final String charset) {
        AuthParams.setCredentialCharset(this.params, charset);
    }
}
