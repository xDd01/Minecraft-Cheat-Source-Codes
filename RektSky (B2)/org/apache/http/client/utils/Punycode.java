package org.apache.http.client.utils;

import org.apache.http.annotation.*;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class Punycode
{
    private static final Idn impl;
    
    public static String toUnicode(final String punycode) {
        return Punycode.impl.toUnicode(punycode);
    }
    
    static {
        Idn _impl;
        try {
            _impl = new JdkIdn();
        }
        catch (Exception e) {
            _impl = new Rfc3492Idn();
        }
        impl = _impl;
    }
}
