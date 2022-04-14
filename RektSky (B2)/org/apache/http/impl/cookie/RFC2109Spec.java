package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.message.*;
import java.util.*;
import org.apache.http.cookie.*;

@Obsolete
@Contract(threading = ThreadingBehavior.SAFE)
public class RFC2109Spec extends CookieSpecBase
{
    static final String[] DATE_PATTERNS;
    private final boolean oneHeader;
    
    public RFC2109Spec(final String[] datepatterns, final boolean oneHeader) {
        super(new CommonCookieAttributeHandler[] { new RFC2109VersionHandler(), new BasicPathHandler() {
                @Override
                public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
                    if (!this.match(cookie, origin)) {
                        throw new CookieRestrictionViolationException("Illegal 'path' attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
                    }
                }
            }, new RFC2109DomainHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((datepatterns != null) ? datepatterns.clone() : RFC2109Spec.DATE_PATTERNS) });
        this.oneHeader = oneHeader;
    }
    
    public RFC2109Spec() {
        this(null, false);
    }
    
    protected RFC2109Spec(final boolean oneHeader, final CommonCookieAttributeHandler... handlers) {
        super(handlers);
        this.oneHeader = oneHeader;
    }
    
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
            throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
        }
        final HeaderElement[] elems = header.getElements();
        return this.parse(elems, origin);
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        final String name = cookie.getName();
        if (name.indexOf(32) != -1) {
            throw new CookieRestrictionViolationException("Cookie name may not contain blanks");
        }
        if (name.startsWith("$")) {
            throw new CookieRestrictionViolationException("Cookie name may not start with $");
        }
        super.validate(cookie, origin);
    }
    
    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        List<Cookie> cookieList;
        if (cookies.size() > 1) {
            cookieList = new ArrayList<Cookie>(cookies);
            Collections.sort(cookieList, CookiePathComparator.INSTANCE);
        }
        else {
            cookieList = cookies;
        }
        if (this.oneHeader) {
            return this.doFormatOneHeader(cookieList);
        }
        return this.doFormatManyHeaders(cookieList);
    }
    
    private List<Header> doFormatOneHeader(final List<Cookie> cookies) {
        int version = Integer.MAX_VALUE;
        for (final Cookie cookie : cookies) {
            if (cookie.getVersion() < version) {
                version = cookie.getVersion();
            }
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(40 * cookies.size());
        buffer.append("Cookie");
        buffer.append(": ");
        buffer.append("$Version=");
        buffer.append(Integer.toString(version));
        for (final Cookie cooky : cookies) {
            buffer.append("; ");
            final Cookie cookie2 = cooky;
            this.formatCookieAsVer(buffer, cookie2, version);
        }
        final List<Header> headers = new ArrayList<Header>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }
    
    private List<Header> doFormatManyHeaders(final List<Cookie> cookies) {
        final List<Header> headers = new ArrayList<Header>(cookies.size());
        for (final Cookie cookie : cookies) {
            final int version = cookie.getVersion();
            final CharArrayBuffer buffer = new CharArrayBuffer(40);
            buffer.append("Cookie: ");
            buffer.append("$Version=");
            buffer.append(Integer.toString(version));
            buffer.append("; ");
            this.formatCookieAsVer(buffer, cookie, version);
            headers.add(new BufferedHeader(buffer));
        }
        return headers;
    }
    
    protected void formatParamAsVer(final CharArrayBuffer buffer, final String name, final String value, final int version) {
        buffer.append(name);
        buffer.append("=");
        if (value != null) {
            if (version > 0) {
                buffer.append('\"');
                buffer.append(value);
                buffer.append('\"');
            }
            else {
                buffer.append(value);
            }
        }
    }
    
    protected void formatCookieAsVer(final CharArrayBuffer buffer, final Cookie cookie, final int version) {
        this.formatParamAsVer(buffer, cookie.getName(), cookie.getValue(), version);
        if (cookie.getPath() != null && cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("path")) {
            buffer.append("; ");
            this.formatParamAsVer(buffer, "$Path", cookie.getPath(), version);
        }
        if (cookie.getDomain() != null && cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("domain")) {
            buffer.append("; ");
            this.formatParamAsVer(buffer, "$Domain", cookie.getDomain(), version);
        }
    }
    
    @Override
    public int getVersion() {
        return 1;
    }
    
    @Override
    public Header getVersionHeader() {
        return null;
    }
    
    @Override
    public String toString() {
        return "rfc2109";
    }
    
    static {
        DATE_PATTERNS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy" };
    }
}
