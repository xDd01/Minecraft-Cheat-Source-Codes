package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import java.util.concurrent.*;
import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.cookie.*;
import java.util.*;
import org.apache.http.message.*;

@Contract(threading = ThreadingBehavior.SAFE)
public class RFC6265CookieSpec implements CookieSpec
{
    private static final char PARAM_DELIMITER = ';';
    private static final char COMMA_CHAR = ',';
    private static final char EQUAL_CHAR = '=';
    private static final char DQUOTE_CHAR = '\"';
    private static final char ESCAPE_CHAR = '\\';
    private static final BitSet TOKEN_DELIMS;
    private static final BitSet VALUE_DELIMS;
    private static final BitSet SPECIAL_CHARS;
    private final CookieAttributeHandler[] attribHandlers;
    private final Map<String, CookieAttributeHandler> attribHandlerMap;
    private final TokenParser tokenParser;
    
    protected RFC6265CookieSpec(final CommonCookieAttributeHandler... handlers) {
        this.attribHandlers = handlers.clone();
        this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(handlers.length);
        for (final CommonCookieAttributeHandler handler : handlers) {
            this.attribHandlerMap.put(handler.getAttributeName().toLowerCase(Locale.ROOT), handler);
        }
        this.tokenParser = TokenParser.INSTANCE;
    }
    
    static String getDefaultPath(final CookieOrigin origin) {
        String defaultPath = origin.getPath();
        int lastSlashIndex = defaultPath.lastIndexOf(47);
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                lastSlashIndex = 1;
            }
            defaultPath = defaultPath.substring(0, lastSlashIndex);
        }
        return defaultPath;
    }
    
    static String getDefaultDomain(final CookieOrigin origin) {
        return origin.getHost();
    }
    
    @Override
    public final List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
            throw new MalformedCookieException("Unrecognized cookie header: '" + header.toString() + "'");
        }
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
        final String name = this.tokenParser.parseToken(buffer, cursor, RFC6265CookieSpec.TOKEN_DELIMS);
        if (name.length() == 0) {
            return Collections.emptyList();
        }
        if (cursor.atEnd()) {
            return Collections.emptyList();
        }
        final int valueDelim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (valueDelim != 61) {
            throw new MalformedCookieException("Cookie value is invalid: '" + header.toString() + "'");
        }
        final String value = this.tokenParser.parseValue(buffer, cursor, RFC6265CookieSpec.VALUE_DELIMS);
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        final BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setPath(getDefaultPath(origin));
        cookie.setDomain(getDefaultDomain(origin));
        cookie.setCreationDate(new Date());
        final Map<String, String> attribMap = new LinkedHashMap<String, String>();
        while (!cursor.atEnd()) {
            final String paramName = this.tokenParser.parseToken(buffer, cursor, RFC6265CookieSpec.TOKEN_DELIMS).toLowerCase(Locale.ROOT);
            String paramValue = null;
            if (!cursor.atEnd()) {
                final int paramDelim = buffer.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (paramDelim == 61) {
                    paramValue = this.tokenParser.parseToken(buffer, cursor, RFC6265CookieSpec.VALUE_DELIMS);
                    if (!cursor.atEnd()) {
                        cursor.updatePos(cursor.getPos() + 1);
                    }
                }
            }
            cookie.setAttribute(paramName, paramValue);
            attribMap.put(paramName, paramValue);
        }
        if (attribMap.containsKey("max-age")) {
            attribMap.remove("expires");
        }
        for (final Map.Entry<String, String> entry : attribMap.entrySet()) {
            final String paramName2 = entry.getKey();
            final String paramValue2 = entry.getValue();
            final CookieAttributeHandler handler = this.attribHandlerMap.get(paramName2);
            if (handler != null) {
                handler.parse(cookie, paramValue2);
            }
        }
        return (List<Cookie>)Collections.singletonList(cookie);
    }
    
    @Override
    public final void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        for (final CookieAttributeHandler handler : this.attribHandlers) {
            handler.validate(cookie, origin);
        }
    }
    
    @Override
    public final boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        for (final CookieAttributeHandler handler : this.attribHandlers) {
            if (!handler.match(cookie, origin)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        List<? extends Cookie> sortedCookies;
        if (cookies.size() > 1) {
            sortedCookies = new ArrayList<Cookie>(cookies);
            Collections.sort(sortedCookies, CookiePriorityComparator.INSTANCE);
        }
        else {
            sortedCookies = cookies;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(20 * sortedCookies.size());
        buffer.append("Cookie");
        buffer.append(": ");
        for (int n = 0; n < sortedCookies.size(); ++n) {
            final Cookie cookie = (Cookie)sortedCookies.get(n);
            if (n > 0) {
                buffer.append(';');
                buffer.append(' ');
            }
            buffer.append(cookie.getName());
            final String s = cookie.getValue();
            if (s != null) {
                buffer.append('=');
                if (this.containsSpecialChar(s)) {
                    buffer.append('\"');
                    for (int i = 0; i < s.length(); ++i) {
                        final char ch = s.charAt(i);
                        if (ch == '\"' || ch == '\\') {
                            buffer.append('\\');
                        }
                        buffer.append(ch);
                    }
                    buffer.append('\"');
                }
                else {
                    buffer.append(s);
                }
            }
        }
        final List<Header> headers = new ArrayList<Header>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }
    
    boolean containsSpecialChar(final CharSequence s) {
        return this.containsChars(s, RFC6265CookieSpec.SPECIAL_CHARS);
    }
    
    boolean containsChars(final CharSequence s, final BitSet chars) {
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (chars.get(ch)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final int getVersion() {
        return 0;
    }
    
    @Override
    public final Header getVersionHeader() {
        return null;
    }
    
    static {
        TOKEN_DELIMS = TokenParser.INIT_BITSET(61, 59);
        VALUE_DELIMS = TokenParser.INIT_BITSET(59);
        SPECIAL_CHARS = TokenParser.INIT_BITSET(32, 34, 44, 59, 92);
    }
}
