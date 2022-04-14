package org.apache.http.impl.io;

import org.apache.http.io.*;
import org.apache.http.config.*;
import org.apache.http.util.*;
import org.apache.http.params.*;
import org.apache.http.message.*;
import java.util.*;
import java.io.*;
import org.apache.http.*;

public abstract class AbstractMessageParser<T extends HttpMessage> implements HttpMessageParser<T>
{
    private static final int HEAD_LINE = 0;
    private static final int HEADERS = 1;
    private final SessionInputBuffer sessionBuffer;
    private final MessageConstraints messageConstraints;
    private final List<CharArrayBuffer> headerLines;
    protected final LineParser lineParser;
    private int state;
    private T message;
    
    @Deprecated
    public AbstractMessageParser(final SessionInputBuffer buffer, final LineParser parser, final HttpParams params) {
        Args.notNull(buffer, "Session input buffer");
        Args.notNull(params, "HTTP parameters");
        this.sessionBuffer = buffer;
        this.messageConstraints = HttpParamConfig.getMessageConstraints(params);
        this.lineParser = ((parser != null) ? parser : BasicLineParser.INSTANCE);
        this.headerLines = new ArrayList<CharArrayBuffer>();
        this.state = 0;
    }
    
    public AbstractMessageParser(final SessionInputBuffer buffer, final LineParser lineParser, final MessageConstraints constraints) {
        this.sessionBuffer = Args.notNull(buffer, "Session input buffer");
        this.lineParser = ((lineParser != null) ? lineParser : BasicLineParser.INSTANCE);
        this.messageConstraints = ((constraints != null) ? constraints : MessageConstraints.DEFAULT);
        this.headerLines = new ArrayList<CharArrayBuffer>();
        this.state = 0;
    }
    
    public static Header[] parseHeaders(final SessionInputBuffer inbuffer, final int maxHeaderCount, final int maxLineLen, final LineParser parser) throws HttpException, IOException {
        final List<CharArrayBuffer> headerLines = new ArrayList<CharArrayBuffer>();
        return parseHeaders(inbuffer, maxHeaderCount, maxLineLen, (parser != null) ? parser : BasicLineParser.INSTANCE, headerLines);
    }
    
    public static Header[] parseHeaders(final SessionInputBuffer inbuffer, final int maxHeaderCount, final int maxLineLen, final LineParser parser, final List<CharArrayBuffer> headerLines) throws HttpException, IOException {
        Args.notNull(inbuffer, "Session input buffer");
        Args.notNull(parser, "Line parser");
        Args.notNull(headerLines, "Header line list");
        CharArrayBuffer current = null;
        CharArrayBuffer previous = null;
        while (true) {
            if (current == null) {
                current = new CharArrayBuffer(64);
            }
            else {
                current.clear();
            }
            final int l = inbuffer.readLine(current);
            if (l == -1 || current.length() < 1) {
                final Header[] headers = new Header[headerLines.size()];
                for (int i = 0; i < headerLines.size(); ++i) {
                    final CharArrayBuffer buffer = headerLines.get(i);
                    try {
                        headers[i] = parser.parseHeader(buffer);
                    }
                    catch (ParseException ex) {
                        throw new ProtocolException(ex.getMessage());
                    }
                }
                return headers;
            }
            if ((current.charAt(0) == ' ' || current.charAt(0) == '\t') && previous != null) {
                int i;
                for (i = 0; i < current.length(); ++i) {
                    final char ch = current.charAt(i);
                    if (ch != ' ' && ch != '\t') {
                        break;
                    }
                }
                if (maxLineLen > 0 && previous.length() + 1 + current.length() - i > maxLineLen) {
                    throw new MessageConstraintException("Maximum line length limit exceeded");
                }
                previous.append(' ');
                previous.append(current, i, current.length() - i);
            }
            else {
                headerLines.add(current);
                previous = current;
                current = null;
            }
            if (maxHeaderCount > 0 && headerLines.size() >= maxHeaderCount) {
                throw new MessageConstraintException("Maximum header count exceeded");
            }
        }
    }
    
    protected abstract T parseHead(final SessionInputBuffer p0) throws IOException, HttpException, ParseException;
    
    @Override
    public T parse() throws IOException, HttpException {
        final int st = this.state;
        switch (st) {
            case 0: {
                try {
                    this.message = this.parseHead(this.sessionBuffer);
                }
                catch (ParseException px) {
                    throw new ProtocolException(px.getMessage(), px);
                }
                this.state = 1;
            }
            case 1: {
                final Header[] headers = parseHeaders(this.sessionBuffer, this.messageConstraints.getMaxHeaderCount(), this.messageConstraints.getMaxLineLength(), this.lineParser, this.headerLines);
                this.message.setHeaders(headers);
                final T result = this.message;
                this.message = null;
                this.headerLines.clear();
                this.state = 0;
                return result;
            }
            default: {
                throw new IllegalStateException("Inconsistent parser state");
            }
        }
    }
}
