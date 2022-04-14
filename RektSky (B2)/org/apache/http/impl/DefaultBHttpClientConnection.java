package org.apache.http.impl;

import java.nio.charset.*;
import org.apache.http.config.*;
import org.apache.http.entity.*;
import org.apache.http.io.*;
import org.apache.http.impl.io.*;
import java.net.*;
import org.apache.http.util.*;
import org.apache.http.*;
import java.io.*;

public class DefaultBHttpClientConnection extends BHttpConnectionBase implements HttpClientConnection
{
    private final HttpMessageParser<HttpResponse> responseParser;
    private final HttpMessageWriter<HttpRequest> requestWriter;
    
    public DefaultBHttpClientConnection(final int buffersize, final int fragmentSizeHint, final CharsetDecoder chardecoder, final CharsetEncoder charencoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy);
        this.requestWriter = ((requestWriterFactory != null) ? requestWriterFactory : DefaultHttpRequestWriterFactory.INSTANCE).create(this.getSessionOutputBuffer());
        this.responseParser = ((responseParserFactory != null) ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE).create(this.getSessionInputBuffer(), constraints);
    }
    
    public DefaultBHttpClientConnection(final int buffersize, final CharsetDecoder chardecoder, final CharsetEncoder charencoder, final MessageConstraints constraints) {
        this(buffersize, buffersize, chardecoder, charencoder, constraints, null, null, null, null);
    }
    
    public DefaultBHttpClientConnection(final int buffersize) {
        this(buffersize, buffersize, null, null, null, null, null, null, null);
    }
    
    protected void onResponseReceived(final HttpResponse response) {
    }
    
    protected void onRequestSubmitted(final HttpRequest request) {
    }
    
    public void bind(final Socket socket) throws IOException {
        super.bind(socket);
    }
    
    @Override
    public boolean isResponseAvailable(final int timeout) throws IOException {
        this.ensureOpen();
        try {
            return this.awaitInput(timeout);
        }
        catch (SocketTimeoutException ex) {
            return false;
        }
    }
    
    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.ensureOpen();
        this.requestWriter.write(request);
        this.onRequestSubmitted(request);
        this.incrementRequestCount();
    }
    
    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.ensureOpen();
        final HttpEntity entity = request.getEntity();
        if (entity == null) {
            return;
        }
        final OutputStream outstream = this.prepareOutput(request);
        entity.writeTo(outstream);
        outstream.close();
    }
    
    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        this.ensureOpen();
        final HttpResponse response = this.responseParser.parse();
        this.onResponseReceived(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.incrementResponseCount();
        }
        return response;
    }
    
    @Override
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.ensureOpen();
        final HttpEntity entity = this.prepareInput(response);
        response.setEntity(entity);
    }
    
    @Override
    public void flush() throws IOException {
        this.ensureOpen();
        this.doFlush();
    }
}
