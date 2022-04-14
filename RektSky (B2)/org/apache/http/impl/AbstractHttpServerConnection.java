package org.apache.http.impl;

import org.apache.http.entity.*;
import org.apache.http.impl.entity.*;
import org.apache.http.params.*;
import org.apache.http.impl.io.*;
import org.apache.http.message.*;
import org.apache.http.io.*;
import org.apache.http.util.*;
import java.io.*;
import org.apache.http.*;

@Deprecated
public abstract class AbstractHttpServerConnection implements HttpServerConnection
{
    private final EntitySerializer entityserializer;
    private final EntityDeserializer entitydeserializer;
    private SessionInputBuffer inbuffer;
    private SessionOutputBuffer outbuffer;
    private EofSensor eofSensor;
    private HttpMessageParser<HttpRequest> requestParser;
    private HttpMessageWriter<HttpResponse> responseWriter;
    private HttpConnectionMetricsImpl metrics;
    
    public AbstractHttpServerConnection() {
        this.inbuffer = null;
        this.outbuffer = null;
        this.eofSensor = null;
        this.requestParser = null;
        this.responseWriter = null;
        this.metrics = null;
        this.entityserializer = this.createEntitySerializer();
        this.entitydeserializer = this.createEntityDeserializer();
    }
    
    protected abstract void assertOpen() throws IllegalStateException;
    
    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0)));
    }
    
    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }
    
    protected HttpRequestFactory createHttpRequestFactory() {
        return DefaultHttpRequestFactory.INSTANCE;
    }
    
    protected HttpMessageParser<HttpRequest> createRequestParser(final SessionInputBuffer buffer, final HttpRequestFactory requestFactory, final HttpParams params) {
        return new DefaultHttpRequestParser(buffer, null, requestFactory, params);
    }
    
    protected HttpMessageWriter<HttpResponse> createResponseWriter(final SessionOutputBuffer buffer, final HttpParams params) {
        return new HttpResponseWriter(buffer, null, params);
    }
    
    protected HttpConnectionMetricsImpl createConnectionMetrics(final HttpTransportMetrics inTransportMetric, final HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }
    
    protected void init(final SessionInputBuffer inbuffer, final SessionOutputBuffer outbuffer, final HttpParams params) {
        this.inbuffer = Args.notNull(inbuffer, "Input session buffer");
        this.outbuffer = Args.notNull(outbuffer, "Output session buffer");
        if (inbuffer instanceof EofSensor) {
            this.eofSensor = (EofSensor)inbuffer;
        }
        this.requestParser = this.createRequestParser(inbuffer, this.createHttpRequestFactory(), params);
        this.responseWriter = this.createResponseWriter(outbuffer, params);
        this.metrics = this.createConnectionMetrics(inbuffer.getMetrics(), outbuffer.getMetrics());
    }
    
    @Override
    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        this.assertOpen();
        final HttpRequest request = this.requestParser.parse();
        this.metrics.incrementRequestCount();
        return request;
    }
    
    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.assertOpen();
        final HttpEntity entity = this.entitydeserializer.deserialize(this.inbuffer, request);
        request.setEntity(entity);
    }
    
    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }
    
    @Override
    public void flush() throws IOException {
        this.assertOpen();
        this.doFlush();
    }
    
    @Override
    public void sendResponseHeader(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.assertOpen();
        this.responseWriter.write(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
    }
    
    @Override
    public void sendResponseEntity(final HttpResponse response) throws HttpException, IOException {
        if (response.getEntity() == null) {
            return;
        }
        this.entityserializer.serialize(this.outbuffer, response, response.getEntity());
    }
    
    protected boolean isEof() {
        return this.eofSensor != null && this.eofSensor.isEof();
    }
    
    @Override
    public boolean isStale() {
        if (!this.isOpen()) {
            return true;
        }
        if (this.isEof()) {
            return true;
        }
        try {
            this.inbuffer.isDataAvailable(1);
            return this.isEof();
        }
        catch (IOException ex) {
            return true;
        }
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
