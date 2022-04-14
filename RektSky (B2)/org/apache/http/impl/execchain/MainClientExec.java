package org.apache.http.impl.execchain;

import org.apache.http.annotation.*;
import org.apache.http.impl.auth.*;
import org.apache.commons.logging.*;
import org.apache.http.client.protocol.*;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.*;
import java.util.concurrent.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.impl.conn.*;
import java.io.*;
import org.apache.http.conn.*;
import org.apache.http.client.config.*;
import org.apache.http.conn.routing.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.apache.http.entity.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class MainClientExec implements ClientExecChain
{
    private final Log log;
    private final HttpRequestExecutor requestExecutor;
    private final HttpClientConnectionManager connManager;
    private final ConnectionReuseStrategy reuseStrategy;
    private final ConnectionKeepAliveStrategy keepAliveStrategy;
    private final HttpProcessor proxyHttpProcessor;
    private final AuthenticationStrategy targetAuthStrategy;
    private final AuthenticationStrategy proxyAuthStrategy;
    private final HttpAuthenticator authenticator;
    private final UserTokenHandler userTokenHandler;
    private final HttpRouteDirector routeDirector;
    
    public MainClientExec(final HttpRequestExecutor requestExecutor, final HttpClientConnectionManager connManager, final ConnectionReuseStrategy reuseStrategy, final ConnectionKeepAliveStrategy keepAliveStrategy, final HttpProcessor proxyHttpProcessor, final AuthenticationStrategy targetAuthStrategy, final AuthenticationStrategy proxyAuthStrategy, final UserTokenHandler userTokenHandler) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(requestExecutor, "HTTP request executor");
        Args.notNull(connManager, "Client connection manager");
        Args.notNull(reuseStrategy, "Connection reuse strategy");
        Args.notNull(keepAliveStrategy, "Connection keep alive strategy");
        Args.notNull(proxyHttpProcessor, "Proxy HTTP processor");
        Args.notNull(targetAuthStrategy, "Target authentication strategy");
        Args.notNull(proxyAuthStrategy, "Proxy authentication strategy");
        Args.notNull(userTokenHandler, "User token handler");
        this.authenticator = new HttpAuthenticator();
        this.routeDirector = new BasicRouteDirector();
        this.requestExecutor = requestExecutor;
        this.connManager = connManager;
        this.reuseStrategy = reuseStrategy;
        this.keepAliveStrategy = keepAliveStrategy;
        this.proxyHttpProcessor = proxyHttpProcessor;
        this.targetAuthStrategy = targetAuthStrategy;
        this.proxyAuthStrategy = proxyAuthStrategy;
        this.userTokenHandler = userTokenHandler;
    }
    
    public MainClientExec(final HttpRequestExecutor requestExecutor, final HttpClientConnectionManager connManager, final ConnectionReuseStrategy reuseStrategy, final ConnectionKeepAliveStrategy keepAliveStrategy, final AuthenticationStrategy targetAuthStrategy, final AuthenticationStrategy proxyAuthStrategy, final UserTokenHandler userTokenHandler) {
        this(requestExecutor, connManager, reuseStrategy, keepAliveStrategy, new ImmutableHttpProcessor(new HttpRequestInterceptor[] { new RequestTargetHost() }), targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        AuthState targetAuthState = context.getTargetAuthState();
        if (targetAuthState == null) {
            targetAuthState = new AuthState();
            context.setAttribute("http.auth.target-scope", targetAuthState);
        }
        AuthState proxyAuthState = context.getProxyAuthState();
        if (proxyAuthState == null) {
            proxyAuthState = new AuthState();
            context.setAttribute("http.auth.proxy-scope", proxyAuthState);
        }
        if (request instanceof HttpEntityEnclosingRequest) {
            RequestEntityProxy.enhance((HttpEntityEnclosingRequest)request);
        }
        Object userToken = context.getUserToken();
        final ConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
        if (execAware != null) {
            if (execAware.isAborted()) {
                connRequest.cancel();
                throw new RequestAbortedException("Request aborted");
            }
            execAware.setCancellable(connRequest);
        }
        final RequestConfig config = context.getRequestConfig();
        HttpClientConnection managedConn;
        try {
            final int timeout = config.getConnectionRequestTimeout();
            managedConn = connRequest.get((timeout > 0) ? ((long)timeout) : 0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new RequestAbortedException("Request aborted", interrupted);
        }
        catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
            throw new RequestAbortedException("Request execution failed", cause);
        }
        context.setAttribute("http.connection", managedConn);
        if (config.isStaleConnectionCheckEnabled() && managedConn.isOpen()) {
            this.log.debug("Stale connection check");
            if (managedConn.isStale()) {
                this.log.debug("Stale connection detected");
                managedConn.close();
            }
        }
        final ConnectionHolder connHolder = new ConnectionHolder(this.log, this.connManager, managedConn);
        try {
            if (execAware != null) {
                execAware.setCancellable(connHolder);
            }
            int execCount = 1;
            while (execCount <= 1 || RequestEntityProxy.isRepeatable(request)) {
                if (execAware != null && execAware.isAborted()) {
                    throw new RequestAbortedException("Request aborted");
                }
                HttpResponse response = null;
                Label_1024: {
                    if (!managedConn.isOpen()) {
                        this.log.debug("Opening connection " + route);
                        try {
                            this.establishRoute(proxyAuthState, managedConn, route, request, context);
                        }
                        catch (TunnelRefusedException ex2) {
                            if (this.log.isDebugEnabled()) {
                                this.log.debug(ex2.getMessage());
                            }
                            response = ex2.getResponse();
                            break Label_1024;
                        }
                    }
                    final int timeout2 = config.getSocketTimeout();
                    if (timeout2 >= 0) {
                        managedConn.setSocketTimeout(timeout2);
                    }
                    if (execAware != null && execAware.isAborted()) {
                        throw new RequestAbortedException("Request aborted");
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Executing request " + request.getRequestLine());
                    }
                    if (!request.containsHeader("Authorization")) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Target auth state: " + targetAuthState.getState());
                        }
                        this.authenticator.generateAuthResponse(request, targetAuthState, context);
                    }
                    if (!request.containsHeader("Proxy-Authorization") && !route.isTunnelled()) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Proxy auth state: " + proxyAuthState.getState());
                        }
                        this.authenticator.generateAuthResponse(request, proxyAuthState, context);
                    }
                    response = this.requestExecutor.execute(request, managedConn, context);
                    if (this.reuseStrategy.keepAlive(response, context)) {
                        final long duration = this.keepAliveStrategy.getKeepAliveDuration(response, context);
                        if (this.log.isDebugEnabled()) {
                            String s;
                            if (duration > 0L) {
                                s = "for " + duration + " " + TimeUnit.MILLISECONDS;
                            }
                            else {
                                s = "indefinitely";
                            }
                            this.log.debug("Connection can be kept alive " + s);
                        }
                        connHolder.setValidFor(duration, TimeUnit.MILLISECONDS);
                        connHolder.markReusable();
                    }
                    else {
                        connHolder.markNonReusable();
                    }
                    if (this.needAuthentication(targetAuthState, proxyAuthState, route, response, context)) {
                        final HttpEntity entity = response.getEntity();
                        if (connHolder.isReusable()) {
                            EntityUtils.consume(entity);
                        }
                        else {
                            managedConn.close();
                            if (proxyAuthState.getState() == AuthProtocolState.SUCCESS && proxyAuthState.isConnectionBased()) {
                                this.log.debug("Resetting proxy auth state");
                                proxyAuthState.reset();
                            }
                            if (targetAuthState.getState() == AuthProtocolState.SUCCESS && targetAuthState.isConnectionBased()) {
                                this.log.debug("Resetting target auth state");
                                targetAuthState.reset();
                            }
                        }
                        final HttpRequest original = request.getOriginal();
                        if (!original.containsHeader("Authorization")) {
                            request.removeHeaders("Authorization");
                        }
                        if (!original.containsHeader("Proxy-Authorization")) {
                            request.removeHeaders("Proxy-Authorization");
                        }
                        ++execCount;
                        continue;
                    }
                }
                if (userToken == null) {
                    userToken = this.userTokenHandler.getUserToken(context);
                    context.setAttribute("http.user-token", userToken);
                }
                if (userToken != null) {
                    connHolder.setState(userToken);
                }
                final HttpEntity entity2 = response.getEntity();
                if (entity2 == null || !entity2.isStreaming()) {
                    connHolder.releaseConnection();
                    return new HttpResponseProxy(response, null);
                }
                return new HttpResponseProxy(response, connHolder);
            }
            throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
        }
        catch (ConnectionShutdownException ex3) {
            final InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
            ioex.initCause(ex3);
            throw ioex;
        }
        catch (HttpException ex4) {
            connHolder.abortConnection();
            throw ex4;
        }
        catch (IOException ex5) {
            connHolder.abortConnection();
            if (proxyAuthState.isConnectionBased()) {
                proxyAuthState.reset();
            }
            if (targetAuthState.isConnectionBased()) {
                targetAuthState.reset();
            }
            throw ex5;
        }
        catch (RuntimeException ex6) {
            connHolder.abortConnection();
            if (proxyAuthState.isConnectionBased()) {
                proxyAuthState.reset();
            }
            if (targetAuthState.isConnectionBased()) {
                targetAuthState.reset();
            }
            throw ex6;
        }
        catch (Error error) {
            this.connManager.shutdown();
            throw error;
        }
    }
    
    void establishRoute(final AuthState proxyAuthState, final HttpClientConnection managedConn, final HttpRoute route, final HttpRequest request, final HttpClientContext context) throws HttpException, IOException {
        final RequestConfig config = context.getRequestConfig();
        final int timeout = config.getConnectTimeout();
        final RouteTracker tracker = new RouteTracker(route);
        int step;
        do {
            final HttpRoute fact = tracker.toRoute();
            step = this.routeDirector.nextStep(route, fact);
            switch (step) {
                case 1: {
                    this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, context);
                    tracker.connectTarget(route.isSecure());
                    continue;
                }
                case 2: {
                    this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, context);
                    final HttpHost proxy = route.getProxyHost();
                    tracker.connectProxy(proxy, false);
                    continue;
                }
                case 3: {
                    final boolean secure = this.createTunnelToTarget(proxyAuthState, managedConn, route, request, context);
                    this.log.debug("Tunnel to target created.");
                    tracker.tunnelTarget(secure);
                    continue;
                }
                case 4: {
                    final int hop = fact.getHopCount() - 1;
                    final boolean secure2 = this.createTunnelToProxy(route, hop, context);
                    this.log.debug("Tunnel to proxy created.");
                    tracker.tunnelProxy(route.getHopTarget(hop), secure2);
                    continue;
                }
                case 5: {
                    this.connManager.upgrade(managedConn, route, context);
                    tracker.layerProtocol(route.isSecure());
                    continue;
                }
                case -1: {
                    throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
                }
                case 0: {
                    this.connManager.routeComplete(managedConn, route, context);
                    continue;
                }
                default: {
                    throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
                }
            }
        } while (step > 0);
    }
    
    private boolean createTunnelToTarget(final AuthState proxyAuthState, final HttpClientConnection managedConn, final HttpRoute route, final HttpRequest request, final HttpClientContext context) throws HttpException, IOException {
        final RequestConfig config = context.getRequestConfig();
        final int timeout = config.getConnectTimeout();
        final HttpHost target = route.getTargetHost();
        final HttpHost proxy = route.getProxyHost();
        HttpResponse response = null;
        final String authority = target.toHostString();
        final HttpRequest connect = new BasicHttpRequest("CONNECT", authority, request.getProtocolVersion());
        this.requestExecutor.preProcess(connect, this.proxyHttpProcessor, context);
        while (response == null) {
            if (!managedConn.isOpen()) {
                this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, context);
            }
            connect.removeHeaders("Proxy-Authorization");
            this.authenticator.generateAuthResponse(connect, proxyAuthState, context);
            response = this.requestExecutor.execute(connect, managedConn, context);
            this.requestExecutor.postProcess(response, this.proxyHttpProcessor, context);
            final int status = response.getStatusLine().getStatusCode();
            if (status < 200) {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            }
            if (!config.isAuthenticationEnabled() || !this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, proxyAuthState, context) || !this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, proxyAuthState, context)) {
                continue;
            }
            if (this.reuseStrategy.keepAlive(response, context)) {
                this.log.debug("Connection kept alive");
                final HttpEntity entity = response.getEntity();
                EntityUtils.consume(entity);
            }
            else {
                managedConn.close();
            }
            response = null;
        }
        final int status = response.getStatusLine().getStatusCode();
        if (status > 299) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                response.setEntity(new BufferedHttpEntity(entity));
            }
            managedConn.close();
            throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
        }
        return false;
    }
    
    private boolean createTunnelToProxy(final HttpRoute route, final int hop, final HttpClientContext context) throws HttpException {
        throw new HttpException("Proxy chains are not supported.");
    }
    
    private boolean needAuthentication(final AuthState targetAuthState, final AuthState proxyAuthState, final HttpRoute route, final HttpResponse response, final HttpClientContext context) {
        final RequestConfig config = context.getRequestConfig();
        if (config.isAuthenticationEnabled()) {
            HttpHost target = context.getTargetHost();
            if (target == null) {
                target = route.getTargetHost();
            }
            if (target.getPort() < 0) {
                target = new HttpHost(target.getHostName(), route.getTargetHost().getPort(), target.getSchemeName());
            }
            final boolean targetAuthRequested = this.authenticator.isAuthenticationRequested(target, response, this.targetAuthStrategy, targetAuthState, context);
            HttpHost proxy = route.getProxyHost();
            if (proxy == null) {
                proxy = route.getTargetHost();
            }
            final boolean proxyAuthRequested = this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, proxyAuthState, context);
            if (targetAuthRequested) {
                return this.authenticator.handleAuthChallenge(target, response, this.targetAuthStrategy, targetAuthState, context);
            }
            if (proxyAuthRequested) {
                return this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, proxyAuthState, context);
            }
        }
        return false;
    }
}
