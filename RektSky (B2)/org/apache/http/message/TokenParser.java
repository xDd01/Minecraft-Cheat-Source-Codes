package org.apache.http.message;

import org.apache.http.annotation.*;
import java.util.*;
import org.apache.http.util.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class TokenParser
{
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final char SP = ' ';
    public static final char HT = '\t';
    public static final char DQUOTE = '\"';
    public static final char ESCAPE = '\\';
    public static final TokenParser INSTANCE;
    
    public static BitSet INIT_BITSET(final int... b) {
        final BitSet bitset = new BitSet();
        for (final int aB : b) {
            bitset.set(aB);
        }
        return bitset;
    }
    
    public static boolean isWhitespace(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }
    
    public String parseToken(final CharArrayBuffer buf, final ParserCursor cursor, final BitSet delimiters) {
        final StringBuilder dst = new StringBuilder();
        boolean whitespace = false;
        while (!cursor.atEnd()) {
            final char current = buf.charAt(cursor.getPos());
            if (delimiters != null && delimiters.get(current)) {
                break;
            }
            if (isWhitespace(current)) {
                this.skipWhiteSpace(buf, cursor);
                whitespace = true;
            }
            else {
                if (whitespace && dst.length() > 0) {
                    dst.append(' ');
                }
                this.copyContent(buf, cursor, delimiters, dst);
                whitespace = false;
            }
        }
        return dst.toString();
    }
    
    public String parseValue(final CharArrayBuffer buf, final ParserCursor cursor, final BitSet delimiters) {
        final StringBuilder dst = new StringBuilder();
        boolean whitespace = false;
        while (!cursor.atEnd()) {
            final char current = buf.charAt(cursor.getPos());
            if (delimiters != null && delimiters.get(current)) {
                break;
            }
            if (isWhitespace(current)) {
                this.skipWhiteSpace(buf, cursor);
                whitespace = true;
            }
            else if (current == '\"') {
                if (whitespace && dst.length() > 0) {
                    dst.append(' ');
                }
                this.copyQuotedContent(buf, cursor, dst);
                whitespace = false;
            }
            else {
                if (whitespace && dst.length() > 0) {
                    dst.append(' ');
                }
                this.copyUnquotedContent(buf, cursor, delimiters, dst);
                whitespace = false;
            }
        }
        return dst.toString();
    }
    
    public void skipWhiteSpace(final CharArrayBuffer buf, final ParserCursor cursor) {
        int pos = cursor.getPos();
        final int indexFrom = cursor.getPos();
        for (int indexTo = cursor.getUpperBound(), i = indexFrom; i < indexTo; ++i) {
            final char current = buf.charAt(i);
            if (!isWhitespace(current)) {
                break;
            }
            ++pos;
        }
        cursor.updatePos(pos);
    }
    
    public void copyContent(final CharArrayBuffer buf, final ParserCursor cursor, final BitSet delimiters, final StringBuilder dst) {
        int pos = cursor.getPos();
        final int indexFrom = cursor.getPos();
        for (int indexTo = cursor.getUpperBound(), i = indexFrom; i < indexTo; ++i) {
            final char current = buf.charAt(i);
            if (delimiters != null && delimiters.get(current)) {
                break;
            }
            if (isWhitespace(current)) {
                break;
            }
            ++pos;
            dst.append(current);
        }
        cursor.updatePos(pos);
    }
    
    public void copyUnquotedContent(final CharArrayBuffer buf, final ParserCursor cursor, final BitSet delimiters, final StringBuilder dst) {
        int pos = cursor.getPos();
        final int indexFrom = cursor.getPos();
        for (int indexTo = cursor.getUpperBound(), i = indexFrom; i < indexTo; ++i) {
            final char current = buf.charAt(i);
            if ((delimiters != null && delimiters.get(current)) || isWhitespace(current)) {
                break;
            }
            if (current == '\"') {
                break;
            }
            ++pos;
            dst.append(current);
        }
        cursor.updatePos(pos);
    }
    
    public void copyQuotedContent(final CharArrayBuffer buf, final ParserCursor cursor, final StringBuilder dst) {
        if (cursor.atEnd()) {
            return;
        }
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        final int indexTo = cursor.getUpperBound();
        char current = buf.charAt(pos);
        if (current != '\"') {
            return;
        }
        ++pos;
        ++indexFrom;
        boolean escaped = false;
        for (int i = indexFrom; i < indexTo; ++i, ++pos) {
            current = buf.charAt(i);
            if (escaped) {
                if (current != '\"' && current != '\\') {
                    dst.append('\\');
                }
                dst.append(current);
                escaped = false;
            }
            else {
                if (current == '\"') {
                    ++pos;
                    break;
                }
                if (current == '\\') {
                    escaped = true;
                }
                else if (current != '\r' && current != '\n') {
                    dst.append(current);
                }
            }
        }
        cursor.updatePos(pos);
    }
    
    static {
        INSTANCE = new TokenParser();
    }
}
