package org.apache.http.client.utils;

import java.net.*;
import java.nio.charset.*;
import org.apache.http.entity.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import java.io.*;
import org.apache.http.message.*;
import java.util.*;
import java.nio.*;
import org.apache.http.*;

public class URLEncodedUtils
{
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final char QP_SEP_A = '&';
    private static final char QP_SEP_S = ';';
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final BitSet UNRESERVED;
    private static final BitSet PUNCT;
    private static final BitSet USERINFO;
    private static final BitSet PATHSAFE;
    private static final BitSet URIC;
    private static final BitSet RESERVED;
    private static final BitSet URLENCODER;
    private static final int RADIX = 16;
    
    @Deprecated
    public static List<NameValuePair> parse(final URI uri, final String charsetName) {
        return parse(uri, (charsetName != null) ? Charset.forName(charsetName) : null);
    }
    
    public static List<NameValuePair> parse(final URI uri, final Charset charset) {
        Args.notNull(uri, "URI");
        final String query = uri.getRawQuery();
        if (query != null && !query.isEmpty()) {
            return parse(query, charset);
        }
        return createEmptyList();
    }
    
    public static List<NameValuePair> parse(final HttpEntity entity) throws IOException {
        Args.notNull(entity, "HTTP entity");
        final ContentType contentType = ContentType.get(entity);
        if (contentType == null || !contentType.getMimeType().equalsIgnoreCase("application/x-www-form-urlencoded")) {
            return createEmptyList();
        }
        final long len = entity.getContentLength();
        Args.check(len <= 2147483647L, "HTTP entity is too large");
        final Charset charset = (contentType.getCharset() != null) ? contentType.getCharset() : HTTP.DEF_CONTENT_CHARSET;
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return createEmptyList();
        }
        CharArrayBuffer buf;
        try {
            buf = new CharArrayBuffer((len > 0L) ? ((int)len) : 1024);
            final Reader reader = new InputStreamReader(instream, charset);
            final char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buf.append(tmp, 0, l);
            }
        }
        finally {
            instream.close();
        }
        if (buf.length() == 0) {
            return createEmptyList();
        }
        return parse(buf, charset, '&');
    }
    
    public static boolean isEncoded(final HttpEntity entity) {
        Args.notNull(entity, "HTTP entity");
        final Header h = entity.getContentType();
        if (h != null) {
            final HeaderElement[] elems = h.getElements();
            if (elems.length > 0) {
                final String contentType = elems[0].getName();
                return contentType.equalsIgnoreCase("application/x-www-form-urlencoded");
            }
        }
        return false;
    }
    
    @Deprecated
    public static void parse(final List<NameValuePair> parameters, final Scanner scanner, final String charset) {
        parse(parameters, scanner, "[&;]", charset);
    }
    
    @Deprecated
    public static void parse(final List<NameValuePair> parameters, final Scanner scanner, final String parameterSepartorPattern, final String charset) {
        scanner.useDelimiter(parameterSepartorPattern);
        while (scanner.hasNext()) {
            final String token = scanner.next();
            final int i = token.indexOf("=");
            String name;
            String value;
            if (i != -1) {
                name = decodeFormFields(token.substring(0, i).trim(), charset);
                value = decodeFormFields(token.substring(i + 1).trim(), charset);
            }
            else {
                name = decodeFormFields(token.trim(), charset);
                value = null;
            }
            parameters.add(new BasicNameValuePair(name, value));
        }
    }
    
    public static List<NameValuePair> parse(final String s, final Charset charset) {
        if (s == null) {
            return createEmptyList();
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, charset, '&', ';');
    }
    
    public static List<NameValuePair> parse(final String s, final Charset charset, final char... separators) {
        if (s == null) {
            return createEmptyList();
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, charset, separators);
    }
    
    public static List<NameValuePair> parse(final CharArrayBuffer buf, final Charset charset, final char... separators) {
        Args.notNull(buf, "Char array buffer");
        final TokenParser tokenParser = TokenParser.INSTANCE;
        final BitSet delimSet = new BitSet();
        for (final char separator : separators) {
            delimSet.set(separator);
        }
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final List<NameValuePair> list = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            delimSet.set(61);
            final String name = tokenParser.parseToken(buf, cursor, delimSet);
            String value = null;
            if (!cursor.atEnd()) {
                final int delim = buf.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (delim == 61) {
                    delimSet.clear(61);
                    value = tokenParser.parseValue(buf, cursor, delimSet);
                    if (!cursor.atEnd()) {
                        cursor.updatePos(cursor.getPos() + 1);
                    }
                }
            }
            if (!name.isEmpty()) {
                list.add(new BasicNameValuePair(decodeFormFields(name, charset), decodeFormFields(value, charset)));
            }
        }
        return list;
    }
    
    public static String format(final List<? extends NameValuePair> parameters, final String charset) {
        return format(parameters, '&', charset);
    }
    
    public static String format(final List<? extends NameValuePair> parameters, final char parameterSeparator, final String charset) {
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters) {
            final String encodedName = encodeFormFields(parameter.getName(), charset);
            final String encodedValue = encodeFormFields(parameter.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append("=");
                result.append(encodedValue);
            }
        }
        return result.toString();
    }
    
    public static String format(final Iterable<? extends NameValuePair> parameters, final Charset charset) {
        return format(parameters, '&', charset);
    }
    
    public static String format(final Iterable<? extends NameValuePair> parameters, final char parameterSeparator, final Charset charset) {
        Args.notNull(parameters, "Parameters");
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters) {
            final String encodedName = encodeFormFields(parameter.getName(), charset);
            final String encodedValue = encodeFormFields(parameter.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append("=");
                result.append(encodedValue);
            }
        }
        return result.toString();
    }
    
    private static List<NameValuePair> createEmptyList() {
        return new ArrayList<NameValuePair>(0);
    }
    
    private static String urlEncode(final String content, final Charset charset, final BitSet safechars, final boolean blankAsPlus) {
        if (content == null) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        final ByteBuffer bb = charset.encode(content);
        while (bb.hasRemaining()) {
            final int b = bb.get() & 0xFF;
            if (safechars.get(b)) {
                buf.append((char)b);
            }
            else if (blankAsPlus && b == 32) {
                buf.append('+');
            }
            else {
                buf.append("%");
                final char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
                final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                buf.append(hex1);
                buf.append(hex2);
            }
        }
        return buf.toString();
    }
    
    private static String urlDecode(final String content, final Charset charset, final boolean plusAsBlank) {
        if (content == null) {
            return null;
        }
        final ByteBuffer bb = ByteBuffer.allocate(content.length());
        final CharBuffer cb = CharBuffer.wrap(content);
        while (cb.hasRemaining()) {
            final char c = cb.get();
            if (c == '%' && cb.remaining() >= 2) {
                final char uc = cb.get();
                final char lc = cb.get();
                final int u = Character.digit(uc, 16);
                final int l = Character.digit(lc, 16);
                if (u != -1 && l != -1) {
                    bb.put((byte)((u << 4) + l));
                }
                else {
                    bb.put((byte)37);
                    bb.put((byte)uc);
                    bb.put((byte)lc);
                }
            }
            else if (plusAsBlank && c == '+') {
                bb.put((byte)32);
            }
            else {
                bb.put((byte)c);
            }
        }
        bb.flip();
        return charset.decode(bb).toString();
    }
    
    private static String decodeFormFields(final String content, final String charset) {
        if (content == null) {
            return null;
        }
        return urlDecode(content, (charset != null) ? Charset.forName(charset) : Consts.UTF_8, true);
    }
    
    private static String decodeFormFields(final String content, final Charset charset) {
        if (content == null) {
            return null;
        }
        return urlDecode(content, (charset != null) ? charset : Consts.UTF_8, true);
    }
    
    private static String encodeFormFields(final String content, final String charset) {
        if (content == null) {
            return null;
        }
        return urlEncode(content, (charset != null) ? Charset.forName(charset) : Consts.UTF_8, URLEncodedUtils.URLENCODER, true);
    }
    
    private static String encodeFormFields(final String content, final Charset charset) {
        if (content == null) {
            return null;
        }
        return urlEncode(content, (charset != null) ? charset : Consts.UTF_8, URLEncodedUtils.URLENCODER, true);
    }
    
    static String encUserInfo(final String content, final Charset charset) {
        return urlEncode(content, charset, URLEncodedUtils.USERINFO, false);
    }
    
    static String encUric(final String content, final Charset charset) {
        return urlEncode(content, charset, URLEncodedUtils.URIC, false);
    }
    
    static String encPath(final String content, final Charset charset) {
        return urlEncode(content, charset, URLEncodedUtils.PATHSAFE, false);
    }
    
    static {
        UNRESERVED = new BitSet(256);
        PUNCT = new BitSet(256);
        USERINFO = new BitSet(256);
        PATHSAFE = new BitSet(256);
        URIC = new BitSet(256);
        RESERVED = new BitSet(256);
        URLENCODER = new BitSet(256);
        for (int i = 97; i <= 122; ++i) {
            URLEncodedUtils.UNRESERVED.set(i);
        }
        for (int i = 65; i <= 90; ++i) {
            URLEncodedUtils.UNRESERVED.set(i);
        }
        for (int i = 48; i <= 57; ++i) {
            URLEncodedUtils.UNRESERVED.set(i);
        }
        URLEncodedUtils.UNRESERVED.set(95);
        URLEncodedUtils.UNRESERVED.set(45);
        URLEncodedUtils.UNRESERVED.set(46);
        URLEncodedUtils.UNRESERVED.set(42);
        URLEncodedUtils.URLENCODER.or(URLEncodedUtils.UNRESERVED);
        URLEncodedUtils.UNRESERVED.set(33);
        URLEncodedUtils.UNRESERVED.set(126);
        URLEncodedUtils.UNRESERVED.set(39);
        URLEncodedUtils.UNRESERVED.set(40);
        URLEncodedUtils.UNRESERVED.set(41);
        URLEncodedUtils.PUNCT.set(44);
        URLEncodedUtils.PUNCT.set(59);
        URLEncodedUtils.PUNCT.set(58);
        URLEncodedUtils.PUNCT.set(36);
        URLEncodedUtils.PUNCT.set(38);
        URLEncodedUtils.PUNCT.set(43);
        URLEncodedUtils.PUNCT.set(61);
        URLEncodedUtils.USERINFO.or(URLEncodedUtils.UNRESERVED);
        URLEncodedUtils.USERINFO.or(URLEncodedUtils.PUNCT);
        URLEncodedUtils.PATHSAFE.or(URLEncodedUtils.UNRESERVED);
        URLEncodedUtils.PATHSAFE.set(47);
        URLEncodedUtils.PATHSAFE.set(59);
        URLEncodedUtils.PATHSAFE.set(58);
        URLEncodedUtils.PATHSAFE.set(64);
        URLEncodedUtils.PATHSAFE.set(38);
        URLEncodedUtils.PATHSAFE.set(61);
        URLEncodedUtils.PATHSAFE.set(43);
        URLEncodedUtils.PATHSAFE.set(36);
        URLEncodedUtils.PATHSAFE.set(44);
        URLEncodedUtils.RESERVED.set(59);
        URLEncodedUtils.RESERVED.set(47);
        URLEncodedUtils.RESERVED.set(63);
        URLEncodedUtils.RESERVED.set(58);
        URLEncodedUtils.RESERVED.set(64);
        URLEncodedUtils.RESERVED.set(38);
        URLEncodedUtils.RESERVED.set(61);
        URLEncodedUtils.RESERVED.set(43);
        URLEncodedUtils.RESERVED.set(36);
        URLEncodedUtils.RESERVED.set(44);
        URLEncodedUtils.RESERVED.set(91);
        URLEncodedUtils.RESERVED.set(93);
        URLEncodedUtils.URIC.or(URLEncodedUtils.RESERVED);
        URLEncodedUtils.URIC.or(URLEncodedUtils.UNRESERVED);
    }
}
