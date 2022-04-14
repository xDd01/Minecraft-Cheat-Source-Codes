package org.apache.http.message;

import java.io.*;
import org.apache.http.util.*;
import java.util.*;
import org.apache.http.*;

public class HeaderGroup implements Cloneable, Serializable
{
    private static final long serialVersionUID = 2608834160639271617L;
    private final Header[] EMPTY;
    private final List<Header> headers;
    
    public HeaderGroup() {
        this.EMPTY = new Header[0];
        this.headers = new ArrayList<Header>(16);
    }
    
    public void clear() {
        this.headers.clear();
    }
    
    public void addHeader(final Header header) {
        if (header == null) {
            return;
        }
        this.headers.add(header);
    }
    
    public void removeHeader(final Header header) {
        if (header == null) {
            return;
        }
        this.headers.remove(header);
    }
    
    public void updateHeader(final Header header) {
        if (header == null) {
            return;
        }
        for (int i = 0; i < this.headers.size(); ++i) {
            final Header current = this.headers.get(i);
            if (current.getName().equalsIgnoreCase(header.getName())) {
                this.headers.set(i, header);
                return;
            }
        }
        this.headers.add(header);
    }
    
    public void setHeaders(final Header[] headers) {
        this.clear();
        if (headers == null) {
            return;
        }
        Collections.addAll(this.headers, headers);
    }
    
    public Header getCondensedHeader(final String name) {
        final Header[] hdrs = this.getHeaders(name);
        if (hdrs.length == 0) {
            return null;
        }
        if (hdrs.length == 1) {
            return hdrs[0];
        }
        final CharArrayBuffer valueBuffer = new CharArrayBuffer(128);
        valueBuffer.append(hdrs[0].getValue());
        for (int i = 1; i < hdrs.length; ++i) {
            valueBuffer.append(", ");
            valueBuffer.append(hdrs[i].getValue());
        }
        return new BasicHeader(name.toLowerCase(Locale.ROOT), valueBuffer.toString());
    }
    
    public Header[] getHeaders(final String name) {
        List<Header> headersFound = null;
        for (int i = 0; i < this.headers.size(); ++i) {
            final Header header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                if (headersFound == null) {
                    headersFound = new ArrayList<Header>();
                }
                headersFound.add(header);
            }
        }
        return (headersFound != null) ? headersFound.toArray(new Header[headersFound.size()]) : this.EMPTY;
    }
    
    public Header getFirstHeader(final String name) {
        for (int i = 0; i < this.headers.size(); ++i) {
            final Header header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }
    
    public Header getLastHeader(final String name) {
        for (int i = this.headers.size() - 1; i >= 0; --i) {
            final Header header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }
    
    public Header[] getAllHeaders() {
        return this.headers.toArray(new Header[this.headers.size()]);
    }
    
    public boolean containsHeader(final String name) {
        for (int i = 0; i < this.headers.size(); ++i) {
            final Header header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public HeaderIterator iterator() {
        return new BasicListHeaderIterator(this.headers, null);
    }
    
    public HeaderIterator iterator(final String name) {
        return new BasicListHeaderIterator(this.headers, name);
    }
    
    public HeaderGroup copy() {
        final HeaderGroup clone = new HeaderGroup();
        clone.headers.addAll(this.headers);
        return clone;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public String toString() {
        return this.headers.toString();
    }
}
