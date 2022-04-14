package org.apache.http.impl.bootstrap;

import java.net.*;
import org.apache.http.config.*;
import javax.net.*;
import javax.net.ssl.*;
import org.apache.http.*;
import org.apache.http.protocol.*;
import org.apache.http.impl.*;
import java.util.*;

public class ServerBootstrap
{
    private int listenerPort;
    private InetAddress localAddress;
    private SocketConfig socketConfig;
    private ConnectionConfig connectionConfig;
    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;
    private String serverInfo;
    private HttpProcessor httpProcessor;
    private ConnectionReuseStrategy connStrategy;
    private HttpResponseFactory responseFactory;
    private HttpRequestHandlerMapper handlerMapper;
    private Map<String, HttpRequestHandler> handlerMap;
    private HttpExpectationVerifier expectationVerifier;
    private ServerSocketFactory serverSocketFactory;
    private SSLContext sslContext;
    private SSLServerSetupHandler sslSetupHandler;
    private HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
    private ExceptionLogger exceptionLogger;
    
    private ServerBootstrap() {
    }
    
    public static ServerBootstrap bootstrap() {
        return new ServerBootstrap();
    }
    
    public final ServerBootstrap setListenerPort(final int listenerPort) {
        this.listenerPort = listenerPort;
        return this;
    }
    
    public final ServerBootstrap setLocalAddress(final InetAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }
    
    public final ServerBootstrap setSocketConfig(final SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
        return this;
    }
    
    public final ServerBootstrap setConnectionConfig(final ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
        return this;
    }
    
    public final ServerBootstrap setHttpProcessor(final HttpProcessor httpProcessor) {
        this.httpProcessor = httpProcessor;
        return this;
    }
    
    public final ServerBootstrap addInterceptorFirst(final HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.responseFirst == null) {
            this.responseFirst = new LinkedList<HttpResponseInterceptor>();
        }
        this.responseFirst.addFirst(itcp);
        return this;
    }
    
    public final ServerBootstrap addInterceptorLast(final HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.responseLast == null) {
            this.responseLast = new LinkedList<HttpResponseInterceptor>();
        }
        this.responseLast.addLast(itcp);
        return this;
    }
    
    public final ServerBootstrap addInterceptorFirst(final HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.requestFirst == null) {
            this.requestFirst = new LinkedList<HttpRequestInterceptor>();
        }
        this.requestFirst.addFirst(itcp);
        return this;
    }
    
    public final ServerBootstrap addInterceptorLast(final HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.requestLast == null) {
            this.requestLast = new LinkedList<HttpRequestInterceptor>();
        }
        this.requestLast.addLast(itcp);
        return this;
    }
    
    public final ServerBootstrap setServerInfo(final String serverInfo) {
        this.serverInfo = serverInfo;
        return this;
    }
    
    public final ServerBootstrap setConnectionReuseStrategy(final ConnectionReuseStrategy connStrategy) {
        this.connStrategy = connStrategy;
        return this;
    }
    
    public final ServerBootstrap setResponseFactory(final HttpResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
        return this;
    }
    
    public final ServerBootstrap setHandlerMapper(final HttpRequestHandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
        return this;
    }
    
    public final ServerBootstrap registerHandler(final String pattern, final HttpRequestHandler handler) {
        if (pattern == null || handler == null) {
            return this;
        }
        if (this.handlerMap == null) {
            this.handlerMap = new HashMap<String, HttpRequestHandler>();
        }
        this.handlerMap.put(pattern, handler);
        return this;
    }
    
    public final ServerBootstrap setExpectationVerifier(final HttpExpectationVerifier expectationVerifier) {
        this.expectationVerifier = expectationVerifier;
        return this;
    }
    
    public final ServerBootstrap setConnectionFactory(final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }
    
    public final ServerBootstrap setSslSetupHandler(final SSLServerSetupHandler sslSetupHandler) {
        this.sslSetupHandler = sslSetupHandler;
        return this;
    }
    
    public final ServerBootstrap setServerSocketFactory(final ServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
        return this;
    }
    
    public final ServerBootstrap setSslContext(final SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }
    
    public final ServerBootstrap setExceptionLogger(final ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
        return this;
    }
    
    public HttpServer create() {
        HttpProcessor httpProcessorCopy = this.httpProcessor;
        if (httpProcessorCopy == null) {
            final HttpProcessorBuilder b = HttpProcessorBuilder.create();
            if (this.requestFirst != null) {
                for (final HttpRequestInterceptor i : this.requestFirst) {
                    b.addFirst(i);
                }
            }
            if (this.responseFirst != null) {
                for (final HttpResponseInterceptor j : this.responseFirst) {
                    b.addFirst(j);
                }
            }
            String serverInfoCopy = this.serverInfo;
            if (serverInfoCopy == null) {
                serverInfoCopy = "Apache-HttpCore/1.1";
            }
            b.addAll(new ResponseDate(), new ResponseServer(serverInfoCopy), new ResponseContent(), new ResponseConnControl());
            if (this.requestLast != null) {
                for (final HttpRequestInterceptor k : this.requestLast) {
                    b.addLast(k);
                }
            }
            if (this.responseLast != null) {
                for (final HttpResponseInterceptor l : this.responseLast) {
                    b.addLast(l);
                }
            }
            httpProcessorCopy = b.build();
        }
        HttpRequestHandlerMapper handlerMapperCopy = this.handlerMapper;
        if (handlerMapperCopy == null) {
            final UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
            if (this.handlerMap != null) {
                for (final Map.Entry<String, HttpRequestHandler> entry : this.handlerMap.entrySet()) {
                    reqistry.register(entry.getKey(), entry.getValue());
                }
            }
            handlerMapperCopy = reqistry;
        }
        ConnectionReuseStrategy connStrategyCopy = this.connStrategy;
        if (connStrategyCopy == null) {
            connStrategyCopy = DefaultConnectionReuseStrategy.INSTANCE;
        }
        HttpResponseFactory responseFactoryCopy = this.responseFactory;
        if (responseFactoryCopy == null) {
            responseFactoryCopy = DefaultHttpResponseFactory.INSTANCE;
        }
        final HttpService httpService = new HttpService(httpProcessorCopy, connStrategyCopy, responseFactoryCopy, handlerMapperCopy, this.expectationVerifier);
        ServerSocketFactory serverSocketFactoryCopy = this.serverSocketFactory;
        if (serverSocketFactoryCopy == null) {
            if (this.sslContext != null) {
                serverSocketFactoryCopy = this.sslContext.getServerSocketFactory();
            }
            else {
                serverSocketFactoryCopy = ServerSocketFactory.getDefault();
            }
        }
        HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactoryCopy = this.connectionFactory;
        if (connectionFactoryCopy == null) {
            if (this.connectionConfig != null) {
                connectionFactoryCopy = new DefaultBHttpServerConnectionFactory(this.connectionConfig);
            }
            else {
                connectionFactoryCopy = DefaultBHttpServerConnectionFactory.INSTANCE;
            }
        }
        ExceptionLogger exceptionLoggerCopy = this.exceptionLogger;
        if (exceptionLoggerCopy == null) {
            exceptionLoggerCopy = ExceptionLogger.NO_OP;
        }
        return new HttpServer((this.listenerPort > 0) ? this.listenerPort : 0, this.localAddress, (this.socketConfig != null) ? this.socketConfig : SocketConfig.DEFAULT, serverSocketFactoryCopy, httpService, connectionFactoryCopy, this.sslSetupHandler, exceptionLoggerCopy);
    }
}
