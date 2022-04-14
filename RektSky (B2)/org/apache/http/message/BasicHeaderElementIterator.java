package org.apache.http.message;

import org.apache.http.util.*;
import org.apache.http.*;
import java.util.*;

public class BasicHeaderElementIterator implements HeaderElementIterator
{
    private final HeaderIterator headerIt;
    private final HeaderValueParser parser;
    private HeaderElement currentElement;
    private CharArrayBuffer buffer;
    private ParserCursor cursor;
    
    public BasicHeaderElementIterator(final HeaderIterator headerIterator, final HeaderValueParser parser) {
        this.currentElement = null;
        this.buffer = null;
        this.cursor = null;
        this.headerIt = Args.notNull(headerIterator, "Header iterator");
        this.parser = Args.notNull(parser, "Parser");
    }
    
    public BasicHeaderElementIterator(final HeaderIterator headerIterator) {
        this(headerIterator, BasicHeaderValueParser.INSTANCE);
    }
    
    private void bufferHeaderValue() {
        this.cursor = null;
        this.buffer = null;
        while (this.headerIt.hasNext()) {
            final Header h = this.headerIt.nextHeader();
            if (h instanceof FormattedHeader) {
                this.buffer = ((FormattedHeader)h).getBuffer();
                (this.cursor = new ParserCursor(0, this.buffer.length())).updatePos(((FormattedHeader)h).getValuePos());
                break;
            }
            final String value = h.getValue();
            if (value != null) {
                (this.buffer = new CharArrayBuffer(value.length())).append(value);
                this.cursor = new ParserCursor(0, this.buffer.length());
                break;
            }
        }
    }
    
    private void parseNextElement() {
        while (this.headerIt.hasNext() || this.cursor != null) {
            if (this.cursor == null || this.cursor.atEnd()) {
                this.bufferHeaderValue();
            }
            if (this.cursor != null) {
                while (!this.cursor.atEnd()) {
                    final HeaderElement e = this.parser.parseHeaderElement(this.buffer, this.cursor);
                    if (e.getName().length() != 0 || e.getValue() != null) {
                        this.currentElement = e;
                        return;
                    }
                }
                if (!this.cursor.atEnd()) {
                    continue;
                }
                this.cursor = null;
                this.buffer = null;
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        if (this.currentElement == null) {
            this.parseNextElement();
        }
        return this.currentElement != null;
    }
    
    @Override
    public HeaderElement nextElement() throws NoSuchElementException {
        if (this.currentElement == null) {
            this.parseNextElement();
        }
        if (this.currentElement == null) {
            throw new NoSuchElementException("No more header elements available");
        }
        final HeaderElement element = this.currentElement;
        this.currentElement = null;
        return element;
    }
    
    @Override
    public final Object next() throws NoSuchElementException {
        return this.nextElement();
    }
    
    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
