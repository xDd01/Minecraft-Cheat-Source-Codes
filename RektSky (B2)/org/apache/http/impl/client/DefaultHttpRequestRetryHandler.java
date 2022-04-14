package org.apache.http.impl.client;

import org.apache.http.client.*;
import org.apache.http.annotation.*;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.http.client.protocol.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler
{
    public static final DefaultHttpRequestRetryHandler INSTANCE;
    private final int retryCount;
    private final boolean requestSentRetryEnabled;
    private final Set<Class<? extends IOException>> nonRetriableClasses;
    
    protected DefaultHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled, final Collection<Class<? extends IOException>> clazzes) {
        this.retryCount = retryCount;
        this.requestSentRetryEnabled = requestSentRetryEnabled;
        this.nonRetriableClasses = new HashSet<Class<? extends IOException>>();
        for (final Class<? extends IOException> clazz : clazzes) {
            this.nonRetriableClasses.add(clazz);
        }
    }
    
    public DefaultHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
        this(retryCount, requestSentRetryEnabled, (Collection<Class<? extends IOException>>)Arrays.asList(InterruptedIOException.class, UnknownHostException.class, ConnectException.class, SSLException.class));
    }
    
    public DefaultHttpRequestRetryHandler() {
        this(3, false);
    }
    
    @Override
    public boolean retryRequest(final IOException exception, final int executionCount, final HttpContext context) {
        Args.notNull(exception, "Exception parameter");
        Args.notNull(context, "HTTP context");
        if (executionCount > this.retryCount) {
            return false;
        }
        if (this.nonRetriableClasses.contains(exception.getClass())) {
            return false;
        }
        for (final Class<? extends IOException> rejectException : this.nonRetriableClasses) {
            if (rejectException.isInstance(exception)) {
                return false;
            }
        }
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final HttpRequest request = clientContext.getRequest();
        return !this.requestIsAborted(request) && (this.handleAsIdempotent(request) || (!clientContext.isRequestSent() || this.requestSentRetryEnabled));
    }
    
    public boolean isRequestSentRetryEnabled() {
        return this.requestSentRetryEnabled;
    }
    
    public int getRetryCount() {
        return this.retryCount;
    }
    
    protected boolean handleAsIdempotent(final HttpRequest request) {
        return !(request instanceof HttpEntityEnclosingRequest);
    }
    
    @Deprecated
    protected boolean requestIsAborted(final HttpRequest request) {
        HttpRequest req = request;
        if (request instanceof RequestWrapper) {
            req = ((RequestWrapper)request).getOriginal();
        }
        return req instanceof HttpUriRequest && ((HttpUriRequest)req).isAborted();
    }
    
    static {
        INSTANCE = new DefaultHttpRequestRetryHandler();
    }
}
