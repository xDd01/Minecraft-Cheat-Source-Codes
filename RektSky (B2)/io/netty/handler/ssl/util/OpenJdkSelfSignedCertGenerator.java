package io.netty.handler.ssl.util;

import java.math.*;
import java.util.*;
import sun.security.x509.*;
import java.security.cert.*;
import java.security.*;

final class OpenJdkSelfSignedCertGenerator
{
    static String[] generate(final String fqdn, final KeyPair keypair, final SecureRandom random) throws Exception {
        final PrivateKey key = keypair.getPrivate();
        final X509CertInfo info = new X509CertInfo();
        final X500Name owner = new X500Name("CN=" + fqdn);
        info.set("version", new CertificateVersion(2));
        info.set("serialNumber", new CertificateSerialNumber(new BigInteger(64, random)));
        try {
            info.set("subject", new CertificateSubjectName(owner));
        }
        catch (CertificateException ignore) {
            info.set("subject", owner);
        }
        try {
            info.set("issuer", new CertificateIssuerName(owner));
        }
        catch (CertificateException ignore) {
            info.set("issuer", owner);
        }
        info.set("validity", new CertificateValidity(SelfSignedCertificate.NOT_BEFORE, SelfSignedCertificate.NOT_AFTER));
        info.set("key", new CertificateX509Key(keypair.getPublic()));
        info.set("algorithmID", new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.sha1WithRSAEncryption_oid)));
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(key, "SHA1withRSA");
        info.set("algorithmID.algorithm", cert.get("x509.algorithm"));
        cert = new X509CertImpl(info);
        cert.sign(key, "SHA1withRSA");
        cert.verify(keypair.getPublic());
        return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, cert);
    }
    
    private OpenJdkSelfSignedCertGenerator() {
    }
}
