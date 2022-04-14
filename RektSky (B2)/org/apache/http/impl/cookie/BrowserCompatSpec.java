package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import org.apache.http.*;
import org.apache.http.cookie.*;
import java.util.*;
import org.apache.http.message.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public class BrowserCompatSpec extends CookieSpecBase
{
    private static final String[] DEFAULT_DATE_PATTERNS;
    
    public BrowserCompatSpec(final String[] datepatterns, final BrowserCompatSpecFactory.SecurityLevel securityLevel) {
        super(new CommonCookieAttributeHandler[] { new BrowserCompatVersionAttributeHandler(), new BasicDomainHandler(), (securityLevel == BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_IE_MEDIUM) ? new BasicPathHandler() {
                @Override
                public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
                }
            } : new BasicPathHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((datepatterns != null) ? datepatterns.clone() : BrowserCompatSpec.DEFAULT_DATE_PATTERNS) });
    }
    
    public BrowserCompatSpec(final String[] datepatterns) {
        this(datepatterns, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
    }
    
    public BrowserCompatSpec() {
        this(null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
    }
    
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        final String headername = header.getName();
        if (!headername.equalsIgnoreCase("Set-Cookie")) {
            throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
        }
        final HeaderElement[] helems = header.getElements();
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
        if (!netscape && versioned) {
            return this.parse(helems, origin);
        }
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
        final HeaderElement elem = parser.parseHeader(buffer, cursor);
        final String name = elem.getName();
        final String value = elem.getValue();
        if (name == null || name.isEmpty()) {
            throw new MalformedCookieException("Cookie name may not be empty");
        }
        final BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setPath(CookieSpecBase.getDefaultPath(origin));
        cookie.setDomain(CookieSpecBase.getDefaultDomain(origin));
        final NameValuePair[] attribs = elem.getParameters();
        for (int j = attribs.length - 1; j >= 0; --j) {
            final NameValuePair attrib = attribs[j];
            final String s2 = attrib.getName().toLowerCase(Locale.ROOT);
            cookie.setAttribute(s2, attrib.getValue());
            final CookieAttributeHandler handler = this.findAttribHandler(s2);
            if (handler != null) {
                handler.parse(cookie, attrib.getValue());
            }
        }
        if (netscape) {
            cookie.setVersion(0);
        }
        return (List<Cookie>)Collections.singletonList(cookie);
    }
    
    private static boolean isQuoteEnclosed(final String s) {
        return s != null && s.startsWith("\"") && s.endsWith("\"");
    }
    
    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        final CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
        buffer.append("Cookie");
        buffer.append(": ");
        for (int i = 0; i < cookies.size(); ++i) {
            final Cookie cookie = cookies.get(i);
            if (i > 0) {
                buffer.append("; ");
            }
            final String cookieName = cookie.getName();
            final String cookieValue = cookie.getValue();
            if (cookie.getVersion() > 0 && !isQuoteEnclosed(cookieValue)) {
                BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(buffer, new BasicHeaderElement(cookieName, cookieValue), false);
            }
            else {
                buffer.append(cookieName);
                buffer.append("=");
                if (cookieValue != null) {
                    buffer.append(cookieValue);
                }
            }
        }
        final List<Header> headers = new ArrayList<Header>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }
    
    @Override
    public int getVersion() {
        return 0;
    }
    
    @Override
    public Header getVersionHeader() {
        return null;
    }
    
    @Override
    public String toString() {
        return "compatibility";
    }
    
    static {
        DEFAULT_DATE_PATTERNS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z" };
    }
}
