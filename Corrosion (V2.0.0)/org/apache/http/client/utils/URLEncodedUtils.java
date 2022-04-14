/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.utils;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Immutable
public class URLEncodedUtils {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final char QP_SEP_A = '&';
    private static final char QP_SEP_S = ';';
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final char[] QP_SEPS;
    private static final String QP_SEP_PATTERN;
    private static final BitSet UNRESERVED;
    private static final BitSet PUNCT;
    private static final BitSet USERINFO;
    private static final BitSet PATHSAFE;
    private static final BitSet URIC;
    private static final BitSet RESERVED;
    private static final BitSet URLENCODER;
    private static final int RADIX = 16;

    public static List<NameValuePair> parse(URI uri, String charset) {
        String query = uri.getRawQuery();
        if (query != null && query.length() > 0) {
            ArrayList<NameValuePair> result = new ArrayList<NameValuePair>();
            Scanner scanner = new Scanner(query);
            URLEncodedUtils.parse(result, scanner, QP_SEP_PATTERN, charset);
            return result;
        }
        return Collections.emptyList();
    }

    public static List<NameValuePair> parse(HttpEntity entity) throws IOException {
        String content;
        ContentType contentType = ContentType.get(entity);
        if (contentType != null && contentType.getMimeType().equalsIgnoreCase(CONTENT_TYPE) && (content = EntityUtils.toString(entity, Consts.ASCII)) != null && content.length() > 0) {
            Charset charset = contentType.getCharset();
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            return URLEncodedUtils.parse(content, charset, QP_SEPS);
        }
        return Collections.emptyList();
    }

    public static boolean isEncoded(HttpEntity entity) {
        HeaderElement[] elems;
        Header h2 = entity.getContentType();
        if (h2 != null && (elems = h2.getElements()).length > 0) {
            String contentType = elems[0].getName();
            return contentType.equalsIgnoreCase(CONTENT_TYPE);
        }
        return false;
    }

    public static void parse(List<NameValuePair> parameters, Scanner scanner, String charset) {
        URLEncodedUtils.parse(parameters, scanner, QP_SEP_PATTERN, charset);
    }

    public static void parse(List<NameValuePair> parameters, Scanner scanner, String parameterSepartorPattern, String charset) {
        scanner.useDelimiter(parameterSepartorPattern);
        while (scanner.hasNext()) {
            String name = null;
            String value = null;
            String token = scanner.next();
            int i2 = token.indexOf(NAME_VALUE_SEPARATOR);
            if (i2 != -1) {
                name = URLEncodedUtils.decodeFormFields(token.substring(0, i2).trim(), charset);
                value = URLEncodedUtils.decodeFormFields(token.substring(i2 + 1).trim(), charset);
            } else {
                name = URLEncodedUtils.decodeFormFields(token.trim(), charset);
            }
            parameters.add(new BasicNameValuePair(name, value));
        }
    }

    public static List<NameValuePair> parse(String s2, Charset charset) {
        return URLEncodedUtils.parse(s2, charset, QP_SEPS);
    }

    public static List<NameValuePair> parse(String s2, Charset charset, char ... parameterSeparator) {
        if (s2 == null) {
            return Collections.emptyList();
        }
        BasicHeaderValueParser parser = BasicHeaderValueParser.INSTANCE;
        CharArrayBuffer buffer = new CharArrayBuffer(s2.length());
        buffer.append(s2);
        ParserCursor cursor = new ParserCursor(0, buffer.length());
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            NameValuePair nvp = parser.parseNameValuePair(buffer, cursor, parameterSeparator);
            if (nvp.getName().length() <= 0) continue;
            list.add(new BasicNameValuePair(URLEncodedUtils.decodeFormFields(nvp.getName(), charset), URLEncodedUtils.decodeFormFields(nvp.getValue(), charset)));
        }
        return list;
    }

    public static String format(List<? extends NameValuePair> parameters, String charset) {
        return URLEncodedUtils.format(parameters, '&', charset);
    }

    public static String format(List<? extends NameValuePair> parameters, char parameterSeparator, String charset) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair nameValuePair : parameters) {
            String encodedName = URLEncodedUtils.encodeFormFields(nameValuePair.getName(), charset);
            String encodedValue = URLEncodedUtils.encodeFormFields(nameValuePair.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue == null) continue;
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    public static String format(Iterable<? extends NameValuePair> parameters, Charset charset) {
        return URLEncodedUtils.format(parameters, '&', charset);
    }

    public static String format(Iterable<? extends NameValuePair> parameters, char parameterSeparator, Charset charset) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair nameValuePair : parameters) {
            String encodedName = URLEncodedUtils.encodeFormFields(nameValuePair.getName(), charset);
            String encodedValue = URLEncodedUtils.encodeFormFields(nameValuePair.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue == null) continue;
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    private static String urlEncode(String content, Charset charset, BitSet safechars, boolean blankAsPlus) {
        if (content == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        ByteBuffer bb2 = charset.encode(content);
        while (bb2.hasRemaining()) {
            int b2 = bb2.get() & 0xFF;
            if (safechars.get(b2)) {
                buf.append((char)b2);
                continue;
            }
            if (blankAsPlus && b2 == 32) {
                buf.append('+');
                continue;
            }
            buf.append("%");
            char hex1 = Character.toUpperCase(Character.forDigit(b2 >> 4 & 0xF, 16));
            char hex2 = Character.toUpperCase(Character.forDigit(b2 & 0xF, 16));
            buf.append(hex1);
            buf.append(hex2);
        }
        return buf.toString();
    }

    private static String urlDecode(String content, Charset charset, boolean plusAsBlank) {
        if (content == null) {
            return null;
        }
        ByteBuffer bb2 = ByteBuffer.allocate(content.length());
        CharBuffer cb2 = CharBuffer.wrap(content);
        while (cb2.hasRemaining()) {
            char c2 = cb2.get();
            if (c2 == '%' && cb2.remaining() >= 2) {
                char uc2 = cb2.get();
                char lc2 = cb2.get();
                int u2 = Character.digit(uc2, 16);
                int l2 = Character.digit(lc2, 16);
                if (u2 != -1 && l2 != -1) {
                    bb2.put((byte)((u2 << 4) + l2));
                    continue;
                }
                bb2.put((byte)37);
                bb2.put((byte)uc2);
                bb2.put((byte)lc2);
                continue;
            }
            if (plusAsBlank && c2 == '+') {
                bb2.put((byte)32);
                continue;
            }
            bb2.put((byte)c2);
        }
        bb2.flip();
        return charset.decode(bb2).toString();
    }

    private static String decodeFormFields(String content, String charset) {
        if (content == null) {
            return null;
        }
        return URLEncodedUtils.urlDecode(content, charset != null ? Charset.forName(charset) : Consts.UTF_8, true);
    }

    private static String decodeFormFields(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        return URLEncodedUtils.urlDecode(content, charset != null ? charset : Consts.UTF_8, true);
    }

    private static String encodeFormFields(String content, String charset) {
        if (content == null) {
            return null;
        }
        return URLEncodedUtils.urlEncode(content, charset != null ? Charset.forName(charset) : Consts.UTF_8, URLENCODER, true);
    }

    private static String encodeFormFields(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        return URLEncodedUtils.urlEncode(content, charset != null ? charset : Consts.UTF_8, URLENCODER, true);
    }

    static String encUserInfo(String content, Charset charset) {
        return URLEncodedUtils.urlEncode(content, charset, USERINFO, false);
    }

    static String encUric(String content, Charset charset) {
        return URLEncodedUtils.urlEncode(content, charset, URIC, false);
    }

    static String encPath(String content, Charset charset) {
        return URLEncodedUtils.urlEncode(content, charset, PATHSAFE, false);
    }

    static {
        int i2;
        QP_SEPS = new char[]{'&', ';'};
        QP_SEP_PATTERN = "[" + new String(QP_SEPS) + "]";
        UNRESERVED = new BitSet(256);
        PUNCT = new BitSet(256);
        USERINFO = new BitSet(256);
        PATHSAFE = new BitSet(256);
        URIC = new BitSet(256);
        RESERVED = new BitSet(256);
        URLENCODER = new BitSet(256);
        for (i2 = 97; i2 <= 122; ++i2) {
            UNRESERVED.set(i2);
        }
        for (i2 = 65; i2 <= 90; ++i2) {
            UNRESERVED.set(i2);
        }
        for (i2 = 48; i2 <= 57; ++i2) {
            UNRESERVED.set(i2);
        }
        UNRESERVED.set(95);
        UNRESERVED.set(45);
        UNRESERVED.set(46);
        UNRESERVED.set(42);
        URLENCODER.or(UNRESERVED);
        UNRESERVED.set(33);
        UNRESERVED.set(126);
        UNRESERVED.set(39);
        UNRESERVED.set(40);
        UNRESERVED.set(41);
        PUNCT.set(44);
        PUNCT.set(59);
        PUNCT.set(58);
        PUNCT.set(36);
        PUNCT.set(38);
        PUNCT.set(43);
        PUNCT.set(61);
        USERINFO.or(UNRESERVED);
        USERINFO.or(PUNCT);
        PATHSAFE.or(UNRESERVED);
        PATHSAFE.set(47);
        PATHSAFE.set(59);
        PATHSAFE.set(58);
        PATHSAFE.set(64);
        PATHSAFE.set(38);
        PATHSAFE.set(61);
        PATHSAFE.set(43);
        PATHSAFE.set(36);
        PATHSAFE.set(44);
        RESERVED.set(59);
        RESERVED.set(47);
        RESERVED.set(63);
        RESERVED.set(58);
        RESERVED.set(64);
        RESERVED.set(38);
        RESERVED.set(61);
        RESERVED.set(43);
        RESERVED.set(36);
        RESERVED.set(44);
        RESERVED.set(91);
        RESERVED.set(93);
        URIC.or(RESERVED);
        URIC.or(UNRESERVED);
    }
}

