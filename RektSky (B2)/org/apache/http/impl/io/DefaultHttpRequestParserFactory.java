package org.apache.http.impl.io;

import org.apache.http.annotation.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.apache.http.impl.*;
import org.apache.http.config.*;
import org.apache.http.io.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpRequestParserFactory implements HttpMessageParserFactory<HttpRequest>
{
    public static final DefaultHttpRequestParserFactory INSTANCE;
    private final LineParser lineParser;
    private final HttpRequestFactory requestFactory;
    
    public DefaultHttpRequestParserFactory(final LineParser lineParser, final HttpRequestFactory requestFactory) {
        this.lineParser = ((lineParser != null) ? lineParser : BasicLineParser.INSTANCE);
        this.requestFactory = ((requestFactory != null) ? requestFactory : DefaultHttpRequestFactory.INSTANCE);
    }
    
    public DefaultHttpRequestParserFactory() {
        this(null, null);
    }
    
    @Override
    public HttpMessageParser<HttpRequest> create(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        return new DefaultHttpRequestParser(buffer, this.lineParser, this.requestFactory, constraints);
    }
    
    static {
        INSTANCE = new DefaultHttpRequestParserFactory();
    }
}
