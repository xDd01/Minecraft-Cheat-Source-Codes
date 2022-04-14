package org.apache.http.impl.cookie;

import org.apache.http.cookie.*;

class RFC6265CookieSpecBase extends RFC6265CookieSpec
{
    RFC6265CookieSpecBase(final CommonCookieAttributeHandler... handlers) {
        super(handlers);
    }
}
