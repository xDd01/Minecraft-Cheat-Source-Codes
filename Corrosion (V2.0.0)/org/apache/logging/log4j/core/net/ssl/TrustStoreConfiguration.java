/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.net.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.net.ssl.StoreConfiguration;
import org.apache.logging.log4j.core.net.ssl.StoreConfigurationException;

@Plugin(name="trustStore", category="Core", printObject=true)
public class TrustStoreConfiguration
extends StoreConfiguration {
    private KeyStore trustStore = null;
    private String trustStoreType = "JKS";

    public TrustStoreConfiguration(String location, String password) {
        super(location, password);
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void load() throws StoreConfigurationException {
        KeyStore ts2 = null;
        InputStream in2 = null;
        LOGGER.debug("Loading truststore from file with params(location={})", this.getLocation());
        try {
            if (this.getLocation() == null) {
                throw new IOException("The location is null");
            }
            ts2 = KeyStore.getInstance(this.trustStoreType);
            in2 = new FileInputStream(this.getLocation());
            ts2.load(in2, this.getPasswordAsCharArray());
        }
        catch (CertificateException e2) {
            try {
                LOGGER.error("No Provider supports a KeyStoreSpi implementation for the specified type {}", this.trustStoreType);
                throw new StoreConfigurationException(e2);
                catch (NoSuchAlgorithmException e3) {
                    LOGGER.error("The algorithm used to check the integrity of the keystore cannot be found");
                    throw new StoreConfigurationException(e3);
                }
                catch (KeyStoreException e4) {
                    LOGGER.error(e4);
                    throw new StoreConfigurationException(e4);
                }
                catch (FileNotFoundException e5) {
                    LOGGER.error("The keystore file({}) is not found", this.getLocation());
                    throw new StoreConfigurationException(e5);
                }
                catch (IOException e6) {
                    LOGGER.error("Something is wrong with the format of the truststore or the given password: {}", e6.getMessage());
                    throw new StoreConfigurationException(e6);
                }
            }
            catch (Throwable throwable) {
                try {
                    if (in2 == null) throw throwable;
                    in2.close();
                    throw throwable;
                }
                catch (Exception e7) {
                    LOGGER.warn("Error closing {}", this.getLocation(), e7);
                }
                throw throwable;
            }
        }
        try {
            if (in2 != null) {
                in2.close();
            }
        }
        catch (Exception e8) {
            LOGGER.warn("Error closing {}", this.getLocation(), e8);
        }
        this.trustStore = ts2;
        LOGGER.debug("Truststore successfully loaded with params(location={})", this.getLocation());
    }

    public KeyStore getTrustStore() throws StoreConfigurationException {
        if (this.trustStore == null) {
            this.load();
        }
        return this.trustStore;
    }

    @PluginFactory
    public static TrustStoreConfiguration createTrustStoreConfiguration(@PluginAttribute(value="location") String location, @PluginAttribute(value="password") String password) {
        return new TrustStoreConfiguration(location, password);
    }
}

