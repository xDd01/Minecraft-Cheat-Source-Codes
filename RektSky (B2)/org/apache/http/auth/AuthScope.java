package org.apache.http.auth;

import org.apache.http.annotation.*;
import org.apache.http.*;
import java.util.*;
import org.apache.http.util.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class AuthScope
{
    public static final String ANY_HOST;
    public static final int ANY_PORT = -1;
    public static final String ANY_REALM;
    public static final String ANY_SCHEME;
    public static final AuthScope ANY;
    private final String scheme;
    private final String realm;
    private final String host;
    private final int port;
    private final HttpHost origin;
    
    public AuthScope(final String host, final int port, final String realm, final String schemeName) {
        this.host = ((host == null) ? AuthScope.ANY_HOST : host.toLowerCase(Locale.ROOT));
        this.port = ((port < 0) ? -1 : port);
        this.realm = ((realm == null) ? AuthScope.ANY_REALM : realm);
        this.scheme = ((schemeName == null) ? AuthScope.ANY_SCHEME : schemeName.toUpperCase(Locale.ROOT));
        this.origin = null;
    }
    
    public AuthScope(final HttpHost origin, final String realm, final String schemeName) {
        Args.notNull(origin, "Host");
        this.host = origin.getHostName().toLowerCase(Locale.ROOT);
        this.port = ((origin.getPort() < 0) ? -1 : origin.getPort());
        this.realm = ((realm == null) ? AuthScope.ANY_REALM : realm);
        this.scheme = ((schemeName == null) ? AuthScope.ANY_SCHEME : schemeName.toUpperCase(Locale.ROOT));
        this.origin = origin;
    }
    
    public AuthScope(final HttpHost origin) {
        this(origin, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);
    }
    
    public AuthScope(final String host, final int port, final String realm) {
        this(host, port, realm, AuthScope.ANY_SCHEME);
    }
    
    public AuthScope(final String host, final int port) {
        this(host, port, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);
    }
    
    public AuthScope(final AuthScope authscope) {
        Args.notNull(authscope, "Scope");
        this.host = authscope.getHost();
        this.port = authscope.getPort();
        this.realm = authscope.getRealm();
        this.scheme = authscope.getScheme();
        this.origin = authscope.getOrigin();
    }
    
    public HttpHost getOrigin() {
        return this.origin;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public int match(final AuthScope that) {
        int factor = 0;
        if (LangUtils.equals(this.scheme, that.scheme)) {
            ++factor;
        }
        else if (this.scheme != AuthScope.ANY_SCHEME && that.scheme != AuthScope.ANY_SCHEME) {
            return -1;
        }
        if (LangUtils.equals(this.realm, that.realm)) {
            factor += 2;
        }
        else if (this.realm != AuthScope.ANY_REALM && that.realm != AuthScope.ANY_REALM) {
            return -1;
        }
        if (this.port == that.port) {
            factor += 4;
        }
        else if (this.port != -1 && that.port != -1) {
            return -1;
        }
        if (LangUtils.equals(this.host, that.host)) {
            factor += 8;
        }
        else if (this.host != AuthScope.ANY_HOST && that.host != AuthScope.ANY_HOST) {
            return -1;
        }
        return factor;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthScope)) {
            return super.equals(o);
        }
        final AuthScope that = (AuthScope)o;
        return LangUtils.equals(this.host, that.host) && this.port == that.port && LangUtils.equals(this.realm, that.realm) && LangUtils.equals(this.scheme, that.scheme);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        if (this.scheme != null) {
            buffer.append(this.scheme.toUpperCase(Locale.ROOT));
            buffer.append(' ');
        }
        if (this.realm != null) {
            buffer.append('\'');
            buffer.append(this.realm);
            buffer.append('\'');
        }
        else {
            buffer.append("<any realm>");
        }
        if (this.host != null) {
            buffer.append('@');
            buffer.append(this.host);
            if (this.port >= 0) {
                buffer.append(':');
                buffer.append(this.port);
            }
        }
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.port);
        hash = LangUtils.hashCode(hash, this.realm);
        hash = LangUtils.hashCode(hash, this.scheme);
        return hash;
    }
    
    static {
        ANY_HOST = null;
        ANY_REALM = null;
        ANY_SCHEME = null;
        ANY = new AuthScope(AuthScope.ANY_HOST, -1, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);
    }
}
