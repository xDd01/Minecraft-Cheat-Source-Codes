package org.apache.http.message;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import java.util.*;
import org.apache.http.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicHeaderValueParser implements HeaderValueParser
{
    @Deprecated
    public static final BasicHeaderValueParser DEFAULT;
    public static final BasicHeaderValueParser INSTANCE;
    private static final char PARAM_DELIMITER = ';';
    private static final char ELEM_DELIMITER = ',';
    private static final BitSet TOKEN_DELIMS;
    private static final BitSet VALUE_DELIMS;
    private final TokenParser tokenParser;
    
    public BasicHeaderValueParser() {
        this.tokenParser = TokenParser.INSTANCE;
    }
    
    public static HeaderElement[] parseElements(final String value, final HeaderValueParser parser) throws ParseException {
        Args.notNull(value, "Value");
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return ((parser != null) ? parser : BasicHeaderValueParser.INSTANCE).parseElements(buffer, cursor);
    }
    
    @Override
    public HeaderElement[] parseElements(final CharArrayBuffer buffer, final ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        final List<HeaderElement> elements = new ArrayList<HeaderElement>();
        while (!cursor.atEnd()) {
            final HeaderElement element = this.parseHeaderElement(buffer, cursor);
            if (element.getName().length() != 0 || element.getValue() != null) {
                elements.add(element);
            }
        }
        return elements.toArray(new HeaderElement[elements.size()]);
    }
    
    public static HeaderElement parseHeaderElement(final String value, final HeaderValueParser parser) throws ParseException {
        Args.notNull(value, "Value");
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return ((parser != null) ? parser : BasicHeaderValueParser.INSTANCE).parseHeaderElement(buffer, cursor);
    }
    
    @Override
    public HeaderElement parseHeaderElement(final CharArrayBuffer buffer, final ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        final NameValuePair nvp = this.parseNameValuePair(buffer, cursor);
        NameValuePair[] params = null;
        if (!cursor.atEnd()) {
            final char ch = buffer.charAt(cursor.getPos() - 1);
            if (ch != ',') {
                params = this.parseParameters(buffer, cursor);
            }
        }
        return this.createHeaderElement(nvp.getName(), nvp.getValue(), params);
    }
    
    protected HeaderElement createHeaderElement(final String name, final String value, final NameValuePair[] params) {
        return new BasicHeaderElement(name, value, params);
    }
    
    public static NameValuePair[] parseParameters(final String value, final HeaderValueParser parser) throws ParseException {
        Args.notNull(value, "Value");
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return ((parser != null) ? parser : BasicHeaderValueParser.INSTANCE).parseParameters(buffer, cursor);
    }
    
    @Override
    public NameValuePair[] parseParameters(final CharArrayBuffer buffer, final ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        this.tokenParser.skipWhiteSpace(buffer, cursor);
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            final NameValuePair param = this.parseNameValuePair(buffer, cursor);
            params.add(param);
            final char ch = buffer.charAt(cursor.getPos() - 1);
            if (ch == ',') {
                break;
            }
        }
        return params.toArray(new NameValuePair[params.size()]);
    }
    
    public static NameValuePair parseNameValuePair(final String value, final HeaderValueParser parser) throws ParseException {
        Args.notNull(value, "Value");
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return ((parser != null) ? parser : BasicHeaderValueParser.INSTANCE).parseNameValuePair(buffer, cursor);
    }
    
    @Override
    public NameValuePair parseNameValuePair(final CharArrayBuffer buffer, final ParserCursor cursor) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        final String name = this.tokenParser.parseToken(buffer, cursor, BasicHeaderValueParser.TOKEN_DELIMS);
        if (cursor.atEnd()) {
            return new BasicNameValuePair(name, null);
        }
        final int delim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (delim != 61) {
            return this.createNameValuePair(name, null);
        }
        final String value = this.tokenParser.parseValue(buffer, cursor, BasicHeaderValueParser.VALUE_DELIMS);
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        return this.createNameValuePair(name, value);
    }
    
    @Deprecated
    public NameValuePair parseNameValuePair(final CharArrayBuffer buffer, final ParserCursor cursor, final char[] delimiters) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        final BitSet delimSet = new BitSet();
        if (delimiters != null) {
            for (final char delimiter : delimiters) {
                delimSet.set(delimiter);
            }
        }
        delimSet.set(61);
        final String name = this.tokenParser.parseToken(buffer, cursor, delimSet);
        if (cursor.atEnd()) {
            return new BasicNameValuePair(name, null);
        }
        final int delim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (delim != 61) {
            return this.createNameValuePair(name, null);
        }
        delimSet.clear(61);
        final String value = this.tokenParser.parseValue(buffer, cursor, delimSet);
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        return this.createNameValuePair(name, value);
    }
    
    protected NameValuePair createNameValuePair(final String name, final String value) {
        return new BasicNameValuePair(name, value);
    }
    
    static {
        DEFAULT = new BasicHeaderValueParser();
        INSTANCE = new BasicHeaderValueParser();
        TOKEN_DELIMS = TokenParser.INIT_BITSET(61, 59, 44);
        VALUE_DELIMS = TokenParser.INIT_BITSET(59, 44);
    }
}
