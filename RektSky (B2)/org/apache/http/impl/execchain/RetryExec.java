package org.apache.http.impl.execchain;

import org.apache.http.annotation.*;
import org.apache.commons.logging.*;
import org.apache.http.util.*;
import org.apache.http.conn.routing.*;
import org.apache.http.client.protocol.*;
import org.apache.http.client.methods.*;
import org.apache.http.protocol.*;
import org.apache.http.client.*;
import java.io.*;
import org.apache.http.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RetryExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final HttpRequestRetryHandler retryHandler;
    
    public RetryExec(final ClientExecChain requestExecutor, final HttpRequestRetryHandler retryHandler) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(requestExecutor, "HTTP request executor");
        Args.notNull(retryHandler, "HTTP request retry handler");
        this.requestExecutor = requestExecutor;
        this.retryHandler = retryHandler;
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        final Header[] origheaders = request.getAllHeaders();
        int execCount = 1;
        try {
            return this.requestExecutor.execute(route, request, context, execAware);
        }
        catch (IOException ex) {
            if (execAware != null && execAware.isAborted()) {
                this.log.debug("Request has been aborted");
                throw ex;
            }
            if (this.retryHandler.retryRequest(ex, execCount, context)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when processing request to " + route + ": " + ex.getMessage());
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug(ex.getMessage(), ex);
                }
                if (!RequestEntityProxy.isRepeatable(request)) {
                    this.log.debug("Cannot retry non-repeatable request");
                    throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity", ex);
                }
                request.setHeaders(origheaders);
                if (this.log.isInfoEnabled()) {
                    this.log.info("Retrying request to " + route);
                }
                ++execCount;
                return this.requestExecutor.execute(route, request, context, execAware);
            }
            else {
                if (ex instanceof NoHttpResponseException) {
                    final NoHttpResponseException updatedex = new NoHttpResponseException(route.getTargetHost().toHostString() + " failed to respond");
                    updatedex.setStackTrace(ex.getStackTrace());
                    throw updatedex;
                }
                throw ex;
            }
        }
    }
}
