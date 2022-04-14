package org.apache.http.conn.ssl;

import org.apache.commons.logging.*;
import org.apache.http.util.*;
import java.security.cert.*;
import java.io.*;
import javax.net.ssl.*;
import org.apache.http.conn.util.*;
import javax.security.auth.x500.*;
import java.util.*;

@Deprecated
public abstract class AbstractVerifier implements X509HostnameVerifier
{
    private final Log log;
    static final String[] BAD_COUNTRY_2LDS;
    
    public AbstractVerifier() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public final void verify(final String host, final SSLSocket ssl) throws IOException {
        Args.notNull(host, "Host");
        SSLSession session = ssl.getSession();
        if (session == null) {
            final InputStream in = ssl.getInputStream();
            in.available();
            session = ssl.getSession();
            if (session == null) {
                ssl.startHandshake();
                session = ssl.getSession();
            }
        }
        final Certificate[] certs = session.getPeerCertificates();
        final X509Certificate x509 = (X509Certificate)certs[0];
        this.verify(host, x509);
    }
    
    @Override
    public final boolean verify(final String host, final SSLSession session) {
        try {
            final Certificate[] certs = session.getPeerCertificates();
            final X509Certificate x509 = (X509Certificate)certs[0];
            this.verify(host, x509);
            return true;
        }
        catch (SSLException ex) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(ex.getMessage(), ex);
            }
            return false;
        }
    }
    
    @Override
    public final void verify(final String host, final X509Certificate cert) throws SSLException {
        final List<SubjectName> allSubjectAltNames = DefaultHostnameVerifier.getSubjectAltNames(cert);
        final List<String> subjectAlts = new ArrayList<String>();
        if (InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host)) {
            for (final SubjectName subjectName : allSubjectAltNames) {
                if (subjectName.getType() == 7) {
                    subjectAlts.add(subjectName.getValue());
                }
            }
        }
        else {
            for (final SubjectName subjectName : allSubjectAltNames) {
                if (subjectName.getType() == 2) {
                    subjectAlts.add(subjectName.getValue());
                }
            }
        }
        final X500Principal subjectPrincipal = cert.getSubjectX500Principal();
        final String cn = DefaultHostnameVerifier.extractCN(subjectPrincipal.getName("RFC2253"));
        this.verify(host, (String[])((cn != null) ? new String[] { cn } : null), (String[])((subjectAlts != null && !subjectAlts.isEmpty()) ? ((String[])subjectAlts.toArray(new String[subjectAlts.size()])) : null));
    }
    
    public final void verify(final String host, final String[] cns, final String[] subjectAlts, final boolean strictWithSubDomains) throws SSLException {
        final String cn = (cns != null && cns.length > 0) ? cns[0] : null;
        final List<String> subjectAltList = (subjectAlts != null && subjectAlts.length > 0) ? Arrays.asList(subjectAlts) : null;
        final String normalizedHost = InetAddressUtils.isIPv6Address(host) ? DefaultHostnameVerifier.normaliseAddress(host.toLowerCase(Locale.ROOT)) : host;
        if (subjectAltList != null) {
            for (final String subjectAlt : subjectAltList) {
                final String normalizedAltSubject = InetAddressUtils.isIPv6Address(subjectAlt) ? DefaultHostnameVerifier.normaliseAddress(subjectAlt) : subjectAlt;
                if (matchIdentity(normalizedHost, normalizedAltSubject, strictWithSubDomains)) {
                    return;
                }
            }
            throw new SSLException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAltList);
        }
        if (cn == null) {
            throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
        }
        final String normalizedCN = InetAddressUtils.isIPv6Address(cn) ? DefaultHostnameVerifier.normaliseAddress(cn) : cn;
        if (matchIdentity(normalizedHost, normalizedCN, strictWithSubDomains)) {
            return;
        }
        throw new SSLException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
    }
    
    private static boolean matchIdentity(final String host, final String identity, final boolean strict) {
        if (host == null) {
            return false;
        }
        final String normalizedHost = host.toLowerCase(Locale.ROOT);
        final String normalizedIdentity = identity.toLowerCase(Locale.ROOT);
        final String[] parts = normalizedIdentity.split("\\.");
        final boolean doWildcard = parts.length >= 3 && parts[0].endsWith("*") && (!strict || validCountryWildcard(parts));
        if (doWildcard) {
            final String firstpart = parts[0];
            boolean match;
            if (firstpart.length() > 1) {
                final String prefix = firstpart.substring(0, firstpart.length() - 1);
                final String suffix = normalizedIdentity.substring(firstpart.length());
                final String hostSuffix = normalizedHost.substring(prefix.length());
                match = (normalizedHost.startsWith(prefix) && hostSuffix.endsWith(suffix));
            }
            else {
                match = normalizedHost.endsWith(normalizedIdentity.substring(1));
            }
            return match && (!strict || countDots(normalizedHost) == countDots(normalizedIdentity));
        }
        return normalizedHost.equals(normalizedIdentity);
    }
    
    private static boolean validCountryWildcard(final String[] parts) {
        return parts.length != 3 || parts[2].length() != 2 || Arrays.binarySearch(AbstractVerifier.BAD_COUNTRY_2LDS, parts[1]) < 0;
    }
    
    public static boolean acceptableCountryWildcard(final String cn) {
        return validCountryWildcard(cn.split("\\."));
    }
    
    public static String[] getCNs(final X509Certificate cert) {
        final String subjectPrincipal = cert.getSubjectX500Principal().toString();
        try {
            final String cn = DefaultHostnameVerifier.extractCN(subjectPrincipal);
            return (String[])((cn != null) ? new String[] { cn } : null);
        }
        catch (SSLException ex) {
            return null;
        }
    }
    
    public static String[] getDNSSubjectAlts(final X509Certificate cert) {
        final List<SubjectName> subjectAltNames = DefaultHostnameVerifier.getSubjectAltNames(cert);
        if (subjectAltNames == null) {
            return null;
        }
        final List<String> dnsAlts = new ArrayList<String>();
        for (final SubjectName subjectName : subjectAltNames) {
            if (subjectName.getType() == 2) {
                dnsAlts.add(subjectName.getValue());
            }
        }
        return (String[])(dnsAlts.isEmpty() ? ((String[])dnsAlts.toArray(new String[dnsAlts.size()])) : null);
    }
    
    public static int countDots(final String s) {
        int count = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '.') {
                ++count;
            }
        }
        return count;
    }
    
    static {
        Arrays.sort(BAD_COUNTRY_2LDS = new String[] { "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org" });
    }
}
