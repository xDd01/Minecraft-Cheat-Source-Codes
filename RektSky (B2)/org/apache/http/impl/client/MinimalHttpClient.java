package org.apache.http.impl.client;

import org.apache.http.annotation.*;
import org.apache.http.impl.execchain.*;
import org.apache.http.util.*;
import org.apache.http.impl.*;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.apache.http.client.protocol.*;
import org.apache.http.conn.routing.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.*;
import org.apache.http.*;
import org.apache.http.client.config.*;
import java.io.*;
import org.apache.http.conn.*;
import java.util.concurrent.*;
import org.apache.http.conn.scheme.*;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
class MinimalHttpClient extends CloseableHttpClient
{
    private final HttpClientConnectionManager connManager;
    private final MinimalClientExec requestExecutor;
    private final HttpParams params;
    
    public MinimalHttpClient(final HttpClientConnectionManager connManager) {
        this.connManager = Args.notNull(connManager, "HTTP connection manager");
        this.requestExecutor = new MinimalClientExec(new HttpRequestExecutor(), connManager, DefaultConnectionReuseStrategy.INSTANCE, DefaultConnectionKeepAliveStrategy.INSTANCE);
        this.params = new BasicHttpParams();
    }
    
    @Override
    protected CloseableHttpResponse doExecute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(target, "Target host");
        Args.notNull(request, "HTTP request");
        HttpExecutionAware execAware = null;
        if (request instanceof HttpExecutionAware) {
            execAware = (HttpExecutionAware)request;
        }
        try {
            final HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request);
            final HttpClientContext localcontext = HttpClientContext.adapt((context != null) ? context : new BasicHttpContext());
            final HttpRoute route = new HttpRoute(target);
            RequestConfig config = null;
            if (request instanceof Configurable) {
                config = ((Configurable)request).getConfig();
            }
            if (config != null) {
                localcontext.setRequestConfig(config);
            }
            return this.requestExecutor.execute(route, wrapper, localcontext, execAware);
        }
        catch (HttpException httpException) {
            throw new ClientProtocolException(httpException);
        }
    }
    
    @Override
    public HttpParams getParams() {
        return this.params;
    }
    
    @Override
    public void close() {
        this.connManager.shutdown();
    }
    
    @Override
    public ClientConnectionManager getConnectionManager() {
        return new ClientConnectionManager() {
            @Override
            public void shutdown() {
                MinimalHttpClient.this.connManager.shutdown();
            }
            
            @Override
            public ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void releaseConnection(final ManagedClientConnection conn, final long validDuration, final TimeUnit timeUnit) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public SchemeRegistry getSchemeRegistry() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void closeIdleConnections(final long idletime, final TimeUnit tunit) {
                MinimalHttpClient.this.connManager.closeIdleConnections(idletime, tunit);
            }
            
            @Override
            public void closeExpiredConnections() {
                MinimalHttpClient.this.connManager.closeExpiredConnections();
            }
        };
    }
}
