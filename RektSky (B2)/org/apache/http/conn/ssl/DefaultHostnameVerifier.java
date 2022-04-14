package org.apache.http.conn.ssl;

import org.apache.http.annotation.*;
import org.apache.commons.logging.*;
import javax.security.auth.x500.*;
import javax.net.ssl.*;
import javax.naming.ldap.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.http.conn.util.*;
import java.security.cert.*;
import java.util.*;
import java.net.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public final class DefaultHostnameVerifier implements HostnameVerifier
{
    private final Log log;
    private final PublicSuffixMatcher publicSuffixMatcher;
    
    public DefaultHostnameVerifier(final PublicSuffixMatcher publicSuffixMatcher) {
        this.log = LogFactory.getLog(this.getClass());
        this.publicSuffixMatcher = publicSuffixMatcher;
    }
    
    public DefaultHostnameVerifier() {
        this(null);
    }
    
    @Override
    public boolean verify(final String host, final SSLSession session) {
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
    
    public void verify(final String host, final X509Certificate cert) throws SSLException {
        final HostNameType hostType = determineHostFormat(host);
        final List<SubjectName> subjectAlts = getSubjectAltNames(cert);
        if (subjectAlts != null && !subjectAlts.isEmpty()) {
            switch (hostType) {
                case IPv4: {
                    matchIPAddress(host, subjectAlts);
                    break;
                }
                case IPv6: {
                    matchIPv6Address(host, subjectAlts);
                    break;
                }
                default: {
                    matchDNSName(host, subjectAlts, this.publicSuffixMatcher);
                    break;
                }
            }
        }
        else {
            final X500Principal subjectPrincipal = cert.getSubjectX500Principal();
            final String cn = extractCN(subjectPrincipal.getName("RFC2253"));
            if (cn == null) {
                throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
            }
            matchCN(host, cn, this.publicSuffixMatcher);
        }
    }
    
    static void matchIPAddress(final String host, final List<SubjectName> subjectAlts) throws SSLException {
        for (int i = 0; i < subjectAlts.size(); ++i) {
            final SubjectName subjectAlt = subjectAlts.get(i);
            if (subjectAlt.getType() == 7 && host.equals(subjectAlt.getValue())) {
                return;
            }
        }
        throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }
    
    static void matchIPv6Address(final String host, final List<SubjectName> subjectAlts) throws SSLException {
        final String normalisedHost = normaliseAddress(host);
        for (int i = 0; i < subjectAlts.size(); ++i) {
            final SubjectName subjectAlt = subjectAlts.get(i);
            if (subjectAlt.getType() == 7) {
                final String normalizedSubjectAlt = normaliseAddress(subjectAlt.getValue());
                if (normalisedHost.equals(normalizedSubjectAlt)) {
                    return;
                }
            }
        }
        throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }
    
    static void matchDNSName(final String host, final List<SubjectName> subjectAlts, final PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
        final String normalizedHost = host.toLowerCase(Locale.ROOT);
        for (int i = 0; i < subjectAlts.size(); ++i) {
            final SubjectName subjectAlt = subjectAlts.get(i);
            if (subjectAlt.getType() == 2) {
                final String normalizedSubjectAlt = subjectAlt.getValue().toLowerCase(Locale.ROOT);
                if (matchIdentityStrict(normalizedHost, normalizedSubjectAlt, publicSuffixMatcher)) {
                    return;
                }
            }
        }
        throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }
    
    static void matchCN(final String host, final String cn, final PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
        final String normalizedHost = host.toLowerCase(Locale.ROOT);
        final String normalizedCn = cn.toLowerCase(Locale.ROOT);
        if (!matchIdentityStrict(normalizedHost, normalizedCn, publicSuffixMatcher)) {
            throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
        }
    }
    
    static boolean matchDomainRoot(final String host, final String domainRoot) {
        return domainRoot != null && host.endsWith(domainRoot) && (host.length() == domainRoot.length() || host.charAt(host.length() - domainRoot.length() - 1) == '.');
    }
    
    private static boolean matchIdentity(final String host, final String identity, final PublicSuffixMatcher publicSuffixMatcher, final boolean strict) {
        if (publicSuffixMatcher != null && host.contains(".") && !matchDomainRoot(host, publicSuffixMatcher.getDomainRoot(identity, DomainType.ICANN))) {
            return false;
        }
        final int asteriskIdx = identity.indexOf(42);
        if (asteriskIdx == -1) {
            return host.equalsIgnoreCase(identity);
        }
        final String prefix = identity.substring(0, asteriskIdx);
        final String suffix = identity.substring(asteriskIdx + 1);
        if (!prefix.isEmpty() && !host.startsWith(prefix)) {
            return false;
        }
        if (!suffix.isEmpty() && !host.endsWith(suffix)) {
            return false;
        }
        if (strict) {
            final String remainder = host.substring(prefix.length(), host.length() - suffix.length());
            if (remainder.contains(".")) {
                return false;
            }
        }
        return true;
    }
    
    static boolean matchIdentity(final String host, final String identity, final PublicSuffixMatcher publicSuffixMatcher) {
        return matchIdentity(host, identity, publicSuffixMatcher, false);
    }
    
    static boolean matchIdentity(final String host, final String identity) {
        return matchIdentity(host, identity, null, false);
    }
    
    static boolean matchIdentityStrict(final String host, final String identity, final PublicSuffixMatcher publicSuffixMatcher) {
        return matchIdentity(host, identity, publicSuffixMatcher, true);
    }
    
    static boolean matchIdentityStrict(final String host, final String identity) {
        return matchIdentity(host, identity, null, true);
    }
    
    static String extractCN(final String subjectPrincipal) throws SSLException {
        if (subjectPrincipal == null) {
            return null;
        }
        try {
            final LdapName subjectDN = new LdapName(subjectPrincipal);
            final List<Rdn> rdns = subjectDN.getRdns();
            for (int i = rdns.size() - 1; i >= 0; --i) {
                final Rdn rds = rdns.get(i);
                final Attributes attributes = rds.toAttributes();
                final Attribute cn = attributes.get("cn");
                if (cn != null) {
                    try {
                        final Object value = cn.get();
                        if (value != null) {
                            return value.toString();
                        }
                    }
                    catch (NoSuchElementException ignore) {}
                    catch (NamingException ex) {}
                }
            }
            return null;
        }
        catch (InvalidNameException e) {
            throw new SSLException(subjectPrincipal + " is not a valid X500 distinguished name");
        }
    }
    
    static HostNameType determineHostFormat(final String host) {
        if (InetAddressUtils.isIPv4Address(host)) {
            return HostNameType.IPv4;
        }
        String s = host;
        if (s.startsWith("[") && s.endsWith("]")) {
            s = host.substring(1, host.length() - 1);
        }
        if (InetAddressUtils.isIPv6Address(s)) {
            return HostNameType.IPv6;
        }
        return HostNameType.DNS;
    }
    
    static List<SubjectName> getSubjectAltNames(final X509Certificate cert) {
        try {
            final Collection<List<?>> entries = cert.getSubjectAlternativeNames();
            if (entries == null) {
                return Collections.emptyList();
            }
            final List<SubjectName> result = new ArrayList<SubjectName>();
            for (final List<?> entry : entries) {
                final Integer type = (entry.size() >= 2) ? ((Integer)entry.get(0)) : null;
                if (type != null && (type == 2 || type == 7)) {
                    final Object o = entry.get(1);
                    if (o instanceof String) {
                        result.add(new SubjectName((String)o, type));
                    }
                    else if (o instanceof byte[]) {}
                }
            }
            return result;
        }
        catch (CertificateParsingException ignore) {
            return Collections.emptyList();
        }
    }
    
    static String normaliseAddress(final String hostname) {
        if (hostname == null) {
            return hostname;
        }
        try {
            final InetAddress inetAddress = InetAddress.getByName(hostname);
            return inetAddress.getHostAddress();
        }
        catch (UnknownHostException unexpected) {
            return hostname;
        }
    }
    
    enum HostNameType
    {
        IPv4(7), 
        IPv6(7), 
        DNS(2);
        
        final int subjectType;
        
        private HostNameType(final int subjectType) {
            this.subjectType = subjectType;
        }
    }
}
