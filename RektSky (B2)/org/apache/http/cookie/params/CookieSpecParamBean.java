package org.apache.http.cookie.params;

import org.apache.http.params.*;
import java.util.*;

@Deprecated
public class CookieSpecParamBean extends HttpAbstractParamBean
{
    public CookieSpecParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setDatePatterns(final Collection<String> patterns) {
        this.params.setParameter("http.protocol.cookie-datepatterns", patterns);
    }
    
    public void setSingleHeader(final boolean singleHeader) {
        this.params.setBooleanParameter("http.protocol.single-cookie-header", singleHeader);
    }
}
