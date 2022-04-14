package org.apache.http.impl.conn;

import org.apache.http.impl.*;
import org.apache.http.conn.*;
import org.apache.http.protocol.*;
import java.util.*;
import java.nio.charset.*;
import org.apache.http.config.*;
import org.apache.http.entity.*;
import org.apache.http.io.*;
import org.apache.http.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;

public class DefaultManagedHttpClientConnection extends DefaultBHttpClientConnection implements ManagedHttpClientConnection, HttpContext
{
    private final String id;
    private final Map<String, Object> attributes;
    private volatile boolean shutdown;
    
    public DefaultManagedHttpClientConnection(final String id, final int buffersize, final int fragmentSizeHint, final CharsetDecoder chardecoder, final CharsetEncoder charencoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.id = id;
        this.attributes = new ConcurrentHashMap<String, Object>();
    }
    
    public DefaultManagedHttpClientConnection(final String id, final int buffersize) {
        this(id, buffersize, buffersize, null, null, null, null, null, null, null);
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public void shutdown() throws IOException {
        this.shutdown = true;
        super.shutdown();
    }
    
    @Override
    public Object getAttribute(final String id) {
        return this.attributes.get(id);
    }
    
    @Override
    public Object removeAttribute(final String id) {
        return this.attributes.remove(id);
    }
    
    @Override
    public void setAttribute(final String id, final Object obj) {
        this.attributes.put(id, obj);
    }
    
    @Override
    public void bind(final Socket socket) throws IOException {
        if (this.shutdown) {
            socket.close();
            throw new InterruptedIOException("Connection already shutdown");
        }
        super.bind(socket);
    }
    
    @Override
    public Socket getSocket() {
        return super.getSocket();
    }
    
    @Override
    public SSLSession getSSLSession() {
        final Socket socket = super.getSocket();
        if (socket instanceof SSLSocket) {
            return ((SSLSocket)socket).getSession();
        }
        return null;
    }
}
