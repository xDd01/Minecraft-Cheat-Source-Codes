package org.apache.http.impl;

import java.nio.charset.*;
import org.apache.http.config.*;
import org.apache.http.entity.*;
import org.apache.http.io.*;
import org.apache.http.impl.entity.*;
import org.apache.http.impl.io.*;
import java.net.*;
import org.apache.http.util.*;
import org.apache.http.*;
import java.io.*;

public class DefaultBHttpServerConnection extends BHttpConnectionBase implements HttpServerConnection
{
    private final HttpMessageParser<HttpRequest> requestParser;
    private final HttpMessageWriter<HttpResponse> responseWriter;
    
    public DefaultBHttpServerConnection(final int buffersize, final int fragmentSizeHint, final CharsetDecoder chardecoder, final CharsetEncoder charencoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageParserFactory<HttpRequest> requestParserFactory, final HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
        super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, (incomingContentStrategy != null) ? incomingContentStrategy : DisallowIdentityContentLengthStrategy.INSTANCE, outgoingContentStrategy);
        this.requestParser = ((requestParserFactory != null) ? requestParserFactory : DefaultHttpRequestParserFactory.INSTANCE).create(this.getSessionInputBuffer(), constraints);
        this.responseWriter = ((responseWriterFactory != null) ? responseWriterFactory : DefaultHttpResponseWriterFactory.INSTANCE).create(this.getSessionOutputBuffer());
    }
    
    public DefaultBHttpServerConnection(final int buffersize, final CharsetDecoder chardecoder, final CharsetEncoder charencoder, final MessageConstraints constraints) {
        this(buffersize, buffersize, chardecoder, charencoder, constraints, null, null, null, null);
    }
    
    public DefaultBHttpServerConnection(final int buffersize) {
        this(buffersize, buffersize, null, null, null, null, null, null, null);
    }
    
    protected void onRequestReceived(final HttpRequest request) {
    }
    
    protected void onResponseSubmitted(final HttpResponse response) {
    }
    
    public void bind(final Socket socket) throws IOException {
        super.bind(socket);
    }
    
    @Override
    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        this.ensureOpen();
        final HttpRequest request = this.requestParser.parse();
        this.onRequestReceived(request);
        this.incrementRequestCount();
        return request;
    }
    
    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.ensureOpen();
        final HttpEntity entity = this.prepareInput(request);
        request.setEntity(entity);
    }
    
    @Override
    public void sendResponseHeader(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.ensureOpen();
        this.responseWriter.write(response);
        this.onResponseSubmitted(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.incrementResponseCount();
        }
    }
    
    @Override
    public void sendResponseEntity(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.ensureOpen();
        final HttpEntity entity = response.getEntity();
        if (entity == null) {
            return;
        }
        final OutputStream outstream = this.prepareOutput(response);
        entity.writeTo(outstream);
        outstream.close();
    }
    
    @Override
    public void flush() throws IOException {
        this.ensureOpen();
        this.doFlush();
    }
}
