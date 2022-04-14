package org.apache.http.impl.entity;

import org.apache.http.entity.*;
import org.apache.http.annotation.*;
import org.apache.http.util.*;
import org.apache.http.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxContentLengthStrategy implements ContentLengthStrategy
{
    public static final LaxContentLengthStrategy INSTANCE;
    private final int implicitLen;
    
    public LaxContentLengthStrategy(final int implicitLen) {
        this.implicitLen = implicitLen;
    }
    
    public LaxContentLengthStrategy() {
        this(-1);
    }
    
    @Override
    public long determineLength(final HttpMessage message) throws HttpException {
        Args.notNull(message, "HTTP message");
        final Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            HeaderElement[] encodings;
            try {
                encodings = transferEncodingHeader.getElements();
            }
            catch (ParseException px) {
                throw new ProtocolException("Invalid Transfer-Encoding header value: " + transferEncodingHeader, px);
            }
            final int len = encodings.length;
            if ("identity".equalsIgnoreCase(transferEncodingHeader.getValue())) {
                return -1L;
            }
            if (len > 0 && "chunked".equalsIgnoreCase(encodings[len - 1].getName())) {
                return -2L;
            }
            return -1L;
        }
        else {
            final Header contentLengthHeader = message.getFirstHeader("Content-Length");
            if (contentLengthHeader == null) {
                return this.implicitLen;
            }
            long contentlen = -1L;
            final Header[] headers = message.getHeaders("Content-Length");
            int i = headers.length - 1;
            while (i >= 0) {
                final Header header = headers[i];
                try {
                    contentlen = Long.parseLong(header.getValue());
                }
                catch (NumberFormatException ignore) {
                    --i;
                    continue;
                }
                break;
            }
            if (contentlen >= 0L) {
                return contentlen;
            }
            return -1L;
        }
    }
    
    static {
        INSTANCE = new LaxContentLengthStrategy();
    }
}
