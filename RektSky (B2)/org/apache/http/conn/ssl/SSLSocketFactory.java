package org.apache.http.conn.ssl;

import org.apache.http.conn.socket.*;
import org.apache.http.annotation.*;
import org.apache.http.conn.scheme.*;
import java.security.*;
import org.apache.http.protocol.*;
import java.io.*;
import org.apache.http.*;
import org.apache.http.params.*;
import org.apache.http.conn.*;
import javax.net.ssl.*;
import org.apache.http.util.*;
import javax.net.*;
import java.net.*;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class SSLSocketFactory implements LayeredConnectionSocketFactory, SchemeLayeredSocketFactory, LayeredSchemeSocketFactory, LayeredSocketFactory
{
    public static final String TLS = "TLS";
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER;
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER;
    private final javax.net.ssl.SSLSocketFactory socketfactory;
    private final HostNameResolver nameResolver;
    private volatile X509HostnameVerifier hostnameVerifier;
    private final String[] supportedProtocols;
    private final String[] supportedCipherSuites;
    
    public static SSLSocketFactory getSocketFactory() throws SSLInitializationException {
        return new SSLSocketFactory(SSLContexts.createDefault(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    private static String[] split(final String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }
    
    public static SSLSocketFactory getSystemSocketFactory() throws SSLInitializationException {
        return new SSLSocketFactory((javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final String algorithm, final KeyStore keystore, final String keyPassword, final KeyStore truststore, final SecureRandom random, final HostNameResolver nameResolver) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, (char[])((keyPassword != null) ? keyPassword.toCharArray() : null)).loadTrustMaterial(truststore).build(), nameResolver);
    }
    
    public SSLSocketFactory(final String algorithm, final KeyStore keystore, final String keyPassword, final KeyStore truststore, final SecureRandom random, final TrustStrategy trustStrategy, final X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, (char[])((keyPassword != null) ? keyPassword.toCharArray() : null)).loadTrustMaterial(truststore, trustStrategy).build(), hostnameVerifier);
    }
    
    public SSLSocketFactory(final String algorithm, final KeyStore keystore, final String keyPassword, final KeyStore truststore, final SecureRandom random, final X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, (char[])((keyPassword != null) ? keyPassword.toCharArray() : null)).loadTrustMaterial(truststore).build(), hostnameVerifier);
    }
    
    public SSLSocketFactory(final KeyStore keystore, final String keystorePassword, final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().loadKeyMaterial(keystore, (char[])((keystorePassword != null) ? keystorePassword.toCharArray() : null)).loadTrustMaterial(truststore).build(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final KeyStore keystore, final String keystorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().loadKeyMaterial(keystore, (char[])((keystorePassword != null) ? keystorePassword.toCharArray() : null)).build(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().loadTrustMaterial(truststore).build(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final TrustStrategy trustStrategy, final X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build(), hostnameVerifier);
    }
    
    public SSLSocketFactory(final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        this(SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final SSLContext sslContext) {
        this(sslContext, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }
    
    public SSLSocketFactory(final SSLContext sslContext, final HostNameResolver nameResolver) {
        this.socketfactory = sslContext.getSocketFactory();
        this.hostnameVerifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
        this.nameResolver = nameResolver;
        this.supportedProtocols = null;
        this.supportedCipherSuites = null;
    }
    
    public SSLSocketFactory(final SSLContext sslContext, final X509HostnameVerifier hostnameVerifier) {
        this(Args.notNull(sslContext, "SSL context").getSocketFactory(), null, null, hostnameVerifier);
    }
    
    public SSLSocketFactory(final SSLContext sslContext, final String[] supportedProtocols, final String[] supportedCipherSuites, final X509HostnameVerifier hostnameVerifier) {
        this(Args.notNull(sslContext, "SSL context").getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
    }
    
    public SSLSocketFactory(final javax.net.ssl.SSLSocketFactory socketfactory, final X509HostnameVerifier hostnameVerifier) {
        this(socketfactory, null, null, hostnameVerifier);
    }
    
    public SSLSocketFactory(final javax.net.ssl.SSLSocketFactory socketfactory, final String[] supportedProtocols, final String[] supportedCipherSuites, final X509HostnameVerifier hostnameVerifier) {
        this.socketfactory = Args.notNull(socketfactory, "SSL socket factory");
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        this.hostnameVerifier = ((hostnameVerifier != null) ? hostnameVerifier : SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        this.nameResolver = null;
    }
    
    @Override
    public Socket createSocket(final HttpParams params) throws IOException {
        return this.createSocket((HttpContext)null);
    }
    
    @Override
    public Socket createSocket() throws IOException {
        return this.createSocket((HttpContext)null);
    }
    
    @Override
    public Socket connectSocket(final Socket socket, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        Args.notNull(remoteAddress, "Remote address");
        Args.notNull(params, "HTTP parameters");
        HttpHost host;
        if (remoteAddress instanceof HttpInetSocketAddress) {
            host = ((HttpInetSocketAddress)remoteAddress).getHttpHost();
        }
        else {
            host = new HttpHost(remoteAddress.getHostName(), remoteAddress.getPort(), "https");
        }
        final int socketTimeout = HttpConnectionParams.getSoTimeout(params);
        final int connectTimeout = HttpConnectionParams.getConnectionTimeout(params);
        socket.setSoTimeout(socketTimeout);
        return this.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, null);
    }
    
    @Override
    public boolean isSecure(final Socket sock) throws IllegalArgumentException {
        Args.notNull(sock, "Socket");
        Asserts.check(sock instanceof SSLSocket, "Socket not created by this factory");
        Asserts.check(!sock.isClosed(), "Socket is closed");
        return true;
    }
    
    @Override
    public Socket createLayeredSocket(final Socket socket, final String host, final int port, final HttpParams params) throws IOException, UnknownHostException {
        return this.createLayeredSocket(socket, host, port, (HttpContext)null);
    }
    
    @Override
    public Socket createLayeredSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
        return this.createLayeredSocket(socket, host, port, (HttpContext)null);
    }
    
    public void setHostnameVerifier(final X509HostnameVerifier hostnameVerifier) {
        Args.notNull(hostnameVerifier, "Hostname verifier");
        this.hostnameVerifier = hostnameVerifier;
    }
    
    public X509HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    @Override
    public Socket connectSocket(final Socket socket, final String host, final int port, final InetAddress local, final int localPort, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        InetAddress remote;
        if (this.nameResolver != null) {
            remote = this.nameResolver.resolve(host);
        }
        else {
            remote = InetAddress.getByName(host);
        }
        InetSocketAddress localAddress = null;
        if (local != null || localPort > 0) {
            localAddress = new InetSocketAddress(local, (localPort > 0) ? localPort : 0);
        }
        final InetSocketAddress remoteAddress = new HttpInetSocketAddress(new HttpHost(host, port), remote, port);
        return this.connectSocket(socket, remoteAddress, localAddress, params);
    }
    
    @Override
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
        return this.createLayeredSocket(socket, host, port, autoClose);
    }
    
    protected void prepareSocket(final SSLSocket socket) throws IOException {
    }
    
    private void internalPrepareSocket(final SSLSocket socket) throws IOException {
        if (this.supportedProtocols != null) {
            socket.setEnabledProtocols(this.supportedProtocols);
        }
        if (this.supportedCipherSuites != null) {
            socket.setEnabledCipherSuites(this.supportedCipherSuites);
        }
        this.prepareSocket(socket);
    }
    
    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        return javax.net.SocketFactory.getDefault().createSocket();
    }
    
    @Override
    public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException {
        Args.notNull(host, "HTTP host");
        Args.notNull(remoteAddress, "Remote address");
        final Socket sock = (socket != null) ? socket : this.createSocket(context);
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        try {
            sock.connect(remoteAddress, connectTimeout);
        }
        catch (IOException ex) {
            try {
                sock.close();
            }
            catch (IOException ex2) {}
            throw ex;
        }
        if (sock instanceof SSLSocket) {
            final SSLSocket sslsock = (SSLSocket)sock;
            sslsock.startHandshake();
            this.verifyHostname(sslsock, host.getHostName());
            return sock;
        }
        return this.createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
    }
    
    @Override
    public Socket createLayeredSocket(final Socket socket, final String target, final int port, final HttpContext context) throws IOException {
        final SSLSocket sslsock = (SSLSocket)this.socketfactory.createSocket(socket, target, port, true);
        this.internalPrepareSocket(sslsock);
        sslsock.startHandshake();
        this.verifyHostname(sslsock, target);
        return sslsock;
    }
    
    private void verifyHostname(final SSLSocket sslsock, final String hostname) throws IOException {
        try {
            this.hostnameVerifier.verify(hostname, sslsock);
        }
        catch (IOException iox) {
            try {
                sslsock.close();
            }
            catch (Exception ex) {}
            throw iox;
        }
    }
    
    static {
        ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
        STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();
    }
}
