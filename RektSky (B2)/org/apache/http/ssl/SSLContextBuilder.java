package org.apache.http.ssl;

import org.apache.http.util.*;
import java.io.*;
import java.security.cert.*;
import java.util.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class SSLContextBuilder
{
    static final String TLS = "TLS";
    private String protocol;
    private final Set<KeyManager> keyManagers;
    private String keyManagerFactoryAlgorithm;
    private String keyStoreType;
    private final Set<TrustManager> trustManagers;
    private String trustManagerFactoryAlgorithm;
    private SecureRandom secureRandom;
    private Provider provider;
    
    public static SSLContextBuilder create() {
        return new SSLContextBuilder();
    }
    
    public SSLContextBuilder() {
        this.keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        this.keyStoreType = KeyStore.getDefaultType();
        this.trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        this.keyManagers = new LinkedHashSet<KeyManager>();
        this.trustManagers = new LinkedHashSet<TrustManager>();
    }
    
    @Deprecated
    public SSLContextBuilder useProtocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }
    
    public SSLContextBuilder setProtocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }
    
    public SSLContextBuilder setSecureRandom(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }
    
    public SSLContextBuilder setProvider(final Provider provider) {
        this.provider = provider;
        return this;
    }
    
    public SSLContextBuilder setProvider(final String name) {
        this.provider = Security.getProvider(name);
        return this;
    }
    
    public SSLContextBuilder setKeyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }
    
    public SSLContextBuilder setKeyManagerFactoryAlgorithm(final String keyManagerFactoryAlgorithm) {
        this.keyManagerFactoryAlgorithm = keyManagerFactoryAlgorithm;
        return this;
    }
    
    public SSLContextBuilder setTrustManagerFactoryAlgorithm(final String trustManagerFactoryAlgorithm) {
        this.trustManagerFactoryAlgorithm = trustManagerFactoryAlgorithm;
        return this;
    }
    
    public SSLContextBuilder loadTrustMaterial(final KeyStore truststore, final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance((this.trustManagerFactoryAlgorithm == null) ? TrustManagerFactory.getDefaultAlgorithm() : this.trustManagerFactoryAlgorithm);
        tmfactory.init(truststore);
        final TrustManager[] tms = tmfactory.getTrustManagers();
        if (tms != null) {
            if (trustStrategy != null) {
                for (int i = 0; i < tms.length; ++i) {
                    final TrustManager tm = tms[i];
                    if (tm instanceof X509TrustManager) {
                        tms[i] = new TrustManagerDelegate((X509TrustManager)tm, trustStrategy);
                    }
                }
            }
            for (final TrustManager tm2 : tms) {
                this.trustManagers.add(tm2);
            }
        }
        return this;
    }
    
    public SSLContextBuilder loadTrustMaterial(final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        return this.loadTrustMaterial(null, trustStrategy);
    }
    
    public SSLContextBuilder loadTrustMaterial(final File file, final char[] storePassword, final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        Args.notNull(file, "Truststore file");
        final KeyStore trustStore = KeyStore.getInstance(this.keyStoreType);
        final FileInputStream instream = new FileInputStream(file);
        try {
            trustStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadTrustMaterial(trustStore, trustStrategy);
    }
    
    public SSLContextBuilder loadTrustMaterial(final File file, final char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        return this.loadTrustMaterial(file, storePassword, null);
    }
    
    public SSLContextBuilder loadTrustMaterial(final File file) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        return this.loadTrustMaterial(file, null);
    }
    
    public SSLContextBuilder loadTrustMaterial(final URL url, final char[] storePassword, final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        Args.notNull(url, "Truststore URL");
        final KeyStore trustStore = KeyStore.getInstance(this.keyStoreType);
        final InputStream instream = url.openStream();
        try {
            trustStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadTrustMaterial(trustStore, trustStrategy);
    }
    
    public SSLContextBuilder loadTrustMaterial(final URL url, final char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        return this.loadTrustMaterial(url, storePassword, null);
    }
    
    public SSLContextBuilder loadKeyMaterial(final KeyStore keystore, final char[] keyPassword, final PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance((this.keyManagerFactoryAlgorithm == null) ? KeyManagerFactory.getDefaultAlgorithm() : this.keyManagerFactoryAlgorithm);
        kmfactory.init(keystore, keyPassword);
        final KeyManager[] kms = kmfactory.getKeyManagers();
        if (kms != null) {
            if (aliasStrategy != null) {
                for (int i = 0; i < kms.length; ++i) {
                    final KeyManager km = kms[i];
                    if (km instanceof X509ExtendedKeyManager) {
                        kms[i] = new KeyManagerDelegate((X509ExtendedKeyManager)km, aliasStrategy);
                    }
                }
            }
            for (final KeyManager km2 : kms) {
                this.keyManagers.add(km2);
            }
        }
        return this;
    }
    
    public SSLContextBuilder loadKeyMaterial(final KeyStore keystore, final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        return this.loadKeyMaterial(keystore, keyPassword, null);
    }
    
    public SSLContextBuilder loadKeyMaterial(final File file, final char[] storePassword, final char[] keyPassword, final PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        Args.notNull(file, "Keystore file");
        final KeyStore identityStore = KeyStore.getInstance(this.keyStoreType);
        final FileInputStream instream = new FileInputStream(file);
        try {
            identityStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadKeyMaterial(identityStore, keyPassword, aliasStrategy);
    }
    
    public SSLContextBuilder loadKeyMaterial(final File file, final char[] storePassword, final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        return this.loadKeyMaterial(file, storePassword, keyPassword, null);
    }
    
    public SSLContextBuilder loadKeyMaterial(final URL url, final char[] storePassword, final char[] keyPassword, final PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        Args.notNull(url, "Keystore URL");
        final KeyStore identityStore = KeyStore.getInstance(this.keyStoreType);
        final InputStream instream = url.openStream();
        try {
            identityStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadKeyMaterial(identityStore, keyPassword, aliasStrategy);
    }
    
    public SSLContextBuilder loadKeyMaterial(final URL url, final char[] storePassword, final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        return this.loadKeyMaterial(url, storePassword, keyPassword, null);
    }
    
    protected void initSSLContext(final SSLContext sslContext, final Collection<KeyManager> keyManagers, final Collection<TrustManager> trustManagers, final SecureRandom secureRandom) throws KeyManagementException {
        sslContext.init((KeyManager[])(keyManagers.isEmpty() ? null : ((KeyManager[])keyManagers.toArray(new KeyManager[keyManagers.size()]))), (TrustManager[])(trustManagers.isEmpty() ? null : ((TrustManager[])trustManagers.toArray(new TrustManager[trustManagers.size()]))), secureRandom);
    }
    
    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        final String protocolStr = (this.protocol != null) ? this.protocol : "TLS";
        SSLContext sslContext;
        if (this.provider != null) {
            sslContext = SSLContext.getInstance(protocolStr, this.provider);
        }
        else {
            sslContext = SSLContext.getInstance(protocolStr);
        }
        this.initSSLContext(sslContext, this.keyManagers, this.trustManagers, this.secureRandom);
        return sslContext;
    }
    
    @Override
    public String toString() {
        return "[provider=" + this.provider + ", protocol=" + this.protocol + ", keyStoreType=" + this.keyStoreType + ", keyManagerFactoryAlgorithm=" + this.keyManagerFactoryAlgorithm + ", keyManagers=" + this.keyManagers + ", trustManagerFactoryAlgorithm=" + this.trustManagerFactoryAlgorithm + ", trustManagers=" + this.trustManagers + ", secureRandom=" + this.secureRandom + "]";
    }
    
    static class TrustManagerDelegate implements X509TrustManager
    {
        private final X509TrustManager trustManager;
        private final TrustStrategy trustStrategy;
        
        TrustManagerDelegate(final X509TrustManager trustManager, final TrustStrategy trustStrategy) {
            this.trustManager = trustManager;
            this.trustStrategy = trustStrategy;
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            this.trustManager.checkClientTrusted(chain, authType);
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            if (!this.trustStrategy.isTrusted(chain, authType)) {
                this.trustManager.checkServerTrusted(chain, authType);
            }
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return this.trustManager.getAcceptedIssuers();
        }
    }
    
    static class KeyManagerDelegate extends X509ExtendedKeyManager
    {
        private final X509ExtendedKeyManager keyManager;
        private final PrivateKeyStrategy aliasStrategy;
        
        KeyManagerDelegate(final X509ExtendedKeyManager keyManager, final PrivateKeyStrategy aliasStrategy) {
            this.keyManager = keyManager;
            this.aliasStrategy = aliasStrategy;
        }
        
        @Override
        public String[] getClientAliases(final String keyType, final Principal[] issuers) {
            return this.keyManager.getClientAliases(keyType, issuers);
        }
        
        public Map<String, PrivateKeyDetails> getClientAliasMap(final String[] keyTypes, final Principal[] issuers) {
            final Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
            for (final String keyType : keyTypes) {
                final String[] aliases = this.keyManager.getClientAliases(keyType, issuers);
                if (aliases != null) {
                    for (final String alias : aliases) {
                        validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
                    }
                }
            }
            return validAliases;
        }
        
        public Map<String, PrivateKeyDetails> getServerAliasMap(final String keyType, final Principal[] issuers) {
            final Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
            final String[] aliases = this.keyManager.getServerAliases(keyType, issuers);
            if (aliases != null) {
                for (final String alias : aliases) {
                    validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
                }
            }
            return validAliases;
        }
        
        @Override
        public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
            final Map<String, PrivateKeyDetails> validAliases = this.getClientAliasMap(keyTypes, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, socket);
        }
        
        @Override
        public String[] getServerAliases(final String keyType, final Principal[] issuers) {
            return this.keyManager.getServerAliases(keyType, issuers);
        }
        
        @Override
        public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
            final Map<String, PrivateKeyDetails> validAliases = this.getServerAliasMap(keyType, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, socket);
        }
        
        @Override
        public X509Certificate[] getCertificateChain(final String alias) {
            return this.keyManager.getCertificateChain(alias);
        }
        
        @Override
        public PrivateKey getPrivateKey(final String alias) {
            return this.keyManager.getPrivateKey(alias);
        }
        
        @Override
        public String chooseEngineClientAlias(final String[] keyTypes, final Principal[] issuers, final SSLEngine sslEngine) {
            final Map<String, PrivateKeyDetails> validAliases = this.getClientAliasMap(keyTypes, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, null);
        }
        
        @Override
        public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine sslEngine) {
            final Map<String, PrivateKeyDetails> validAliases = this.getServerAliasMap(keyType, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, null);
        }
    }
}
