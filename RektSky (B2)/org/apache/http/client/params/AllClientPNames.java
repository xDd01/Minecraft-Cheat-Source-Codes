package org.apache.http.client.params;

import org.apache.http.params.*;
import org.apache.http.auth.params.*;
import org.apache.http.cookie.params.*;
import org.apache.http.conn.params.*;

@Deprecated
public interface AllClientPNames extends CoreConnectionPNames, CoreProtocolPNames, ClientPNames, AuthPNames, CookieSpecPNames, ConnConnectionPNames, ConnManagerPNames, ConnRoutePNames
{
}
