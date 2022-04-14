package org.apache.http.impl;

import org.apache.http.config.*;
import java.util.concurrent.atomic.*;
import java.nio.charset.*;
import org.apache.http.impl.entity.*;
import java.io.*;
import org.apache.http.io.*;
import org.apache.http.impl.io.*;
import org.apache.http.entity.*;
import org.apache.http.*;
import org.apache.http.util.*;
import java.net.*;

public class BHttpConnectionBase implements HttpConnection, HttpInetConnection
{
    private final SessionInputBufferImpl inbuffer;
    private final SessionOutputBufferImpl outbuffer;
    private final MessageConstraints messageConstraints;
    private final HttpConnectionMetricsImpl connMetrics;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final AtomicReference<Socket> socketHolder;
    
    protected BHttpConnectionBase(final int buffersize, final int fragmentSizeHint, final CharsetDecoder chardecoder, final CharsetEncoder charencoder, final MessageConstraints messageConstraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy) {
        Args.positive(buffersize, "Buffer size");
        final HttpTransportMetricsImpl inTransportMetrics = new HttpTransportMetricsImpl();
        final HttpTransportMetricsImpl outTransportMetrics = new HttpTransportMetricsImpl();
        this.inbuffer = new SessionInputBufferImpl(inTransportMetrics, buffersize, -1, (messageConstraints != null) ? messageConstraints : MessageConstraints.DEFAULT, chardecoder);
        this.outbuffer = new SessionOutputBufferImpl(outTransportMetrics, buffersize, fragmentSizeHint, charencoder);
        this.messageConstraints = messageConstraints;
        this.connMetrics = new HttpConnectionMetricsImpl(inTransportMetrics, outTransportMetrics);
        this.incomingContentStrategy = ((incomingContentStrategy != null) ? incomingContentStrategy : LaxContentLengthStrategy.INSTANCE);
        this.outgoingContentStrategy = ((outgoingContentStrategy != null) ? outgoingContentStrategy : StrictContentLengthStrategy.INSTANCE);
        this.socketHolder = new AtomicReference<Socket>();
    }
    
    protected void ensureOpen() throws IOException {
        final Socket socket = this.socketHolder.get();
        if (socket == null) {
            throw new ConnectionClosedException("Connection is closed");
        }
        if (!this.inbuffer.isBound()) {
            this.inbuffer.bind(this.getSocketInputStream(socket));
        }
        if (!this.outbuffer.isBound()) {
            this.outbuffer.bind(this.getSocketOutputStream(socket));
        }
    }
    
    protected InputStream getSocketInputStream(final Socket socket) throws IOException {
        return socket.getInputStream();
    }
    
    protected OutputStream getSocketOutputStream(final Socket socket) throws IOException {
        return socket.getOutputStream();
    }
    
    protected void bind(final Socket socket) throws IOException {
        Args.notNull(socket, "Socket");
        this.socketHolder.set(socket);
        this.inbuffer.bind(null);
        this.outbuffer.bind(null);
    }
    
    protected SessionInputBuffer getSessionInputBuffer() {
        return this.inbuffer;
    }
    
    protected SessionOutputBuffer getSessionOutputBuffer() {
        return this.outbuffer;
    }
    
    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }
    
    @Override
    public boolean isOpen() {
        return this.socketHolder.get() != null;
    }
    
    protected Socket getSocket() {
        return this.socketHolder.get();
    }
    
    protected OutputStream createOutputStream(final long len, final SessionOutputBuffer outbuffer) {
        if (len == -2L) {
            return new ChunkedOutputStream(2048, outbuffer);
        }
        if (len == -1L) {
            return new IdentityOutputStream(outbuffer);
        }
        return new ContentLengthOutputStream(outbuffer, len);
    }
    
    protected OutputStream prepareOutput(final HttpMessage message) throws HttpException {
        final long len = this.outgoingContentStrategy.determineLength(message);
        return this.createOutputStream(len, this.outbuffer);
    }
    
    protected InputStream createInputStream(final long len, final SessionInputBuffer inbuffer) {
        if (len == -2L) {
            return new ChunkedInputStream(inbuffer, this.messageConstraints);
        }
        if (len == -1L) {
            return new IdentityInputStream(inbuffer);
        }
        if (len == 0L) {
            return EmptyInputStream.INSTANCE;
        }
        return new ContentLengthInputStream(inbuffer, len);
    }
    
    protected HttpEntity prepareInput(final HttpMessage message) throws HttpException {
        final BasicHttpEntity entity = new BasicHttpEntity();
        final long len = this.incomingContentStrategy.determineLength(message);
        final InputStream instream = this.createInputStream(len, this.inbuffer);
        if (len == -2L) {
            entity.setChunked(true);
            entity.setContentLength(-1L);
            entity.setContent(instream);
        }
        else if (len == -1L) {
            entity.setChunked(false);
            entity.setContentLength(-1L);
            entity.setContent(instream);
        }
        else {
            entity.setChunked(false);
            entity.setContentLength(len);
            entity.setContent(instream);
        }
        final Header contentTypeHeader = message.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        final Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
        if (contentEncodingHeader != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }
    
    @Override
    public InetAddress getLocalAddress() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getLocalAddress() : null;
    }
    
    @Override
    public int getLocalPort() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getLocalPort() : -1;
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getInetAddress() : null;
    }
    
    @Override
    public int getRemotePort() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getPort() : -1;
    }
    
    @Override
    public void setSocketTimeout(final int timeout) {
        final Socket socket = this.socketHolder.get();
        if (socket != null) {
            try {
                socket.setSoTimeout(timeout);
            }
            catch (SocketException ex) {}
        }
    }
    
    @Override
    public int getSocketTimeout() {
        final Socket socket = this.socketHolder.get();
        if (socket != null) {
            try {
                return socket.getSoTimeout();
            }
            catch (SocketException ignore) {
                return -1;
            }
        }
        return -1;
    }
    
    @Override
    public void shutdown() throws IOException {
        final Socket socket = this.socketHolder.getAndSet(null);
        if (socket != null) {
            try {
                socket.setSoLinger(true, 0);
            }
            catch (IOException ex) {}
            finally {
                socket.close();
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        final Socket socket = this.socketHolder.getAndSet(null);
        if (socket != null) {
            try {
                this.inbuffer.clear();
                this.outbuffer.flush();
                try {
                    try {
                        socket.shutdownOutput();
                    }
                    catch (IOException ex) {}
                    try {
                        socket.shutdownInput();
                    }
                    catch (IOException ex2) {}
                }
                catch (UnsupportedOperationException ex3) {}
            }
            finally {
                socket.close();
            }
        }
    }
    
    private int fillInputBuffer(final int timeout) throws IOException {
        final Socket socket = this.socketHolder.get();
        final int oldtimeout = socket.getSoTimeout();
        try {
            socket.setSoTimeout(timeout);
            return this.inbuffer.fillBuffer();
        }
        finally {
            socket.setSoTimeout(oldtimeout);
        }
    }
    
    protected boolean awaitInput(final int timeout) throws IOException {
        if (this.inbuffer.hasBufferedData()) {
            return true;
        }
        this.fillInputBuffer(timeout);
        return this.inbuffer.hasBufferedData();
    }
    
    @Override
    public boolean isStale() {
        if (!this.isOpen()) {
            return true;
        }
        try {
            final int bytesRead = this.fillInputBuffer(1);
            return bytesRead < 0;
        }
        catch (SocketTimeoutException ex) {
            return false;
        }
        catch (IOException ex2) {
            return true;
        }
    }
    
    protected void incrementRequestCount() {
        this.connMetrics.incrementRequestCount();
    }
    
    protected void incrementResponseCount() {
        this.connMetrics.incrementResponseCount();
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.connMetrics;
    }
    
    @Override
    public String toString() {
        final Socket socket = this.socketHolder.get();
        if (socket != null) {
            final StringBuilder buffer = new StringBuilder();
            final SocketAddress remoteAddress = socket.getRemoteSocketAddress();
            final SocketAddress localAddress = socket.getLocalSocketAddress();
            if (remoteAddress != null && localAddress != null) {
                NetUtils.formatAddress(buffer, localAddress);
                buffer.append("<->");
                NetUtils.formatAddress(buffer, remoteAddress);
            }
            return buffer.toString();
        }
        return "[Not bound]";
    }
}
