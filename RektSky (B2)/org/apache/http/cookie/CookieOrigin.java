package org.apache.http.cookie;

import org.apache.http.annotation.*;
import java.util.*;
import org.apache.http.util.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class CookieOrigin
{
    private final String host;
    private final int port;
    private final String path;
    private final boolean secure;
    
    public CookieOrigin(final String host, final int port, final String path, final boolean secure) {
        Args.notBlank(host, "Host");
        Args.notNegative(port, "Port");
        Args.notNull(path, "Path");
        this.host = host.toLowerCase(Locale.ROOT);
        this.port = port;
        if (!TextUtils.isBlank(path)) {
            this.path = path;
        }
        else {
            this.path = "/";
        }
        this.secure = secure;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        if (this.secure) {
            buffer.append("(secure)");
        }
        buffer.append(this.host);
        buffer.append(':');
        buffer.append(Integer.toString(this.port));
        buffer.append(this.path);
        buffer.append(']');
        return buffer.toString();
    }
}
