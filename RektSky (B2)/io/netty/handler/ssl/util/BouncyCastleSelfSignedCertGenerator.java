package io.netty.handler.ssl.util;

import org.bouncycastle.asn1.x500.*;
import java.math.*;
import java.util.*;
import org.bouncycastle.operator.jcajce.*;
import org.bouncycastle.cert.jcajce.*;
import java.security.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.cert.*;
import java.security.cert.*;
import org.bouncycastle.jce.provider.*;

final class BouncyCastleSelfSignedCertGenerator
{
    private static final Provider PROVIDER;
    
    static String[] generate(final String fqdn, final KeyPair keypair, final SecureRandom random) throws Exception {
        final PrivateKey key = keypair.getPrivate();
        final X500Name owner = new X500Name("CN=" + fqdn);
        final X509v3CertificateBuilder builder = (X509v3CertificateBuilder)new JcaX509v3CertificateBuilder(owner, new BigInteger(64, random), SelfSignedCertificate.NOT_BEFORE, SelfSignedCertificate.NOT_AFTER, owner, keypair.getPublic());
        final ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(key);
        final X509CertificateHolder certHolder = builder.build(signer);
        final X509Certificate cert = new JcaX509CertificateConverter().setProvider(BouncyCastleSelfSignedCertGenerator.PROVIDER).getCertificate(certHolder);
        cert.verify(keypair.getPublic());
        return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, cert);
    }
    
    private BouncyCastleSelfSignedCertGenerator() {
    }
    
    static {
        PROVIDER = (Provider)new BouncyCastleProvider();
    }
}
