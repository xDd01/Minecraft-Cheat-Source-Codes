package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.message.*;
import org.apache.http.util.*;
import org.apache.http.*;
import org.apache.http.cookie.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.SAFE)
public class DefaultCookieSpec implements CookieSpec
{
    private final RFC2965Spec strict;
    private final RFC2109Spec obsoleteStrict;
    private final NetscapeDraftSpec netscapeDraft;
    
    DefaultCookieSpec(final RFC2965Spec strict, final RFC2109Spec obsoleteStrict, final NetscapeDraftSpec netscapeDraft) {
        this.strict = strict;
        this.obsoleteStrict = obsoleteStrict;
        this.netscapeDraft = netscapeDraft;
    }
    
    public DefaultCookieSpec(final String[] datepatterns, final boolean oneHeader) {
        this.strict = new RFC2965Spec(oneHeader, new CommonCookieAttributeHandler[] { new RFC2965VersionAttributeHandler(), new BasicPathHandler(), new RFC2965DomainAttributeHandler(), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler() });
        this.obsoleteStrict = new RFC2109Spec(oneHeader, new CommonCookieAttributeHandler[] { new RFC2109VersionHandler(), new BasicPathHandler(), new RFC2109DomainHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler() });
        this.netscapeDraft = new NetscapeDraftSpec(new CommonCookieAttributeHandler[] { new BasicDomainHandler(), new BasicPathHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((datepatterns != null) ? datepatterns.clone() : new String[] { "EEE, dd-MMM-yy HH:mm:ss z" }) });
    }
    
    public DefaultCookieSpec() {
        this(null, false);
    }
    
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        HeaderElement[] helems = header.getElements();
        boolean versioned = false;
        boolean netscape = false;
        for (final HeaderElement helem : helems) {
            if (helem.getParameterByName("version") != null) {
                versioned = true;
            }
            if (helem.getParameterByName("expires") != null) {
                netscape = true;
            }
        }
        if (netscape || !versioned) {
            final NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
            CharArrayBuffer buffer;
            ParserCursor cursor;
            if (header instanceof FormattedHeader) {
                buffer = ((FormattedHeader)header).getBuffer();
                cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
            }
            else {
                final String s = header.getValue();
                if (s == null) {
                    throw new MalformedCookieException("Header value is null");
                }
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                cursor = new ParserCursor(0, buffer.length());
            }
            helems = new HeaderElement[] { parser.parseHeader(buffer, cursor) };
            return this.netscapeDraft.parse(helems, origin);
        }
        if ("Set-Cookie2".equals(header.getName())) {
            return this.strict.parse(helems, origin);
        }
        return this.obsoleteStrict.parse(helems, origin);
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        if (cookie.getVersion() > 0) {
            if (cookie instanceof SetCookie2) {
                this.strict.validate(cookie, origin);
            }
            else {
                this.obsoleteStrict.validate(cookie, origin);
            }
        }
        else {
            this.netscapeDraft.validate(cookie, origin);
        }
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        if (cookie.getVersion() <= 0) {
            return this.netscapeDraft.match(cookie, origin);
        }
        if (cookie instanceof SetCookie2) {
            return this.strict.match(cookie, origin);
        }
        return this.obsoleteStrict.match(cookie, origin);
    }
    
    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notNull(cookies, "List of cookies");
        int version = Integer.MAX_VALUE;
        boolean isSetCookie2 = true;
        for (final Cookie cookie : cookies) {
            if (!(cookie instanceof SetCookie2)) {
                isSetCookie2 = false;
            }
            if (cookie.getVersion() < version) {
                version = cookie.getVersion();
            }
        }
        if (version <= 0) {
            return this.netscapeDraft.formatCookies(cookies);
        }
        if (isSetCookie2) {
            return this.strict.formatCookies(cookies);
        }
        return this.obsoleteStrict.formatCookies(cookies);
    }
    
    @Override
    public int getVersion() {
        return this.strict.getVersion();
    }
    
    @Override
    public Header getVersionHeader() {
        return null;
    }
    
    @Override
    public String toString() {
        return "default";
    }
}
