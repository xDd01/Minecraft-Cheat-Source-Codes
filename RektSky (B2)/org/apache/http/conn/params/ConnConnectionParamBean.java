package org.apache.http.conn.params;

import org.apache.http.params.*;

@Deprecated
public class ConnConnectionParamBean extends HttpAbstractParamBean
{
    public ConnConnectionParamBean(final HttpParams params) {
        super(params);
    }
    
    @Deprecated
    public void setMaxStatusLineGarbage(final int maxStatusLineGarbage) {
        this.params.setIntParameter("http.connection.max-status-line-garbage", maxStatusLineGarbage);
    }
}
