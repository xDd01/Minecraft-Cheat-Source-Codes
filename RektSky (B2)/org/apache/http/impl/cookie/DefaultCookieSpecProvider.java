package org.apache.http.impl.cookie;

import org.apache.http.annotation.*;
import org.apache.http.conn.util.*;
import org.apache.http.protocol.*;
import org.apache.http.cookie.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultCookieSpecProvider implements CookieSpecProvider
{
    private final CompatibilityLevel compatibilityLevel;
    private final PublicSuffixMatcher publicSuffixMatcher;
    private final String[] datepatterns;
    private final boolean oneHeader;
    private volatile CookieSpec cookieSpec;
    
    public DefaultCookieSpecProvider(final CompatibilityLevel compatibilityLevel, final PublicSuffixMatcher publicSuffixMatcher, final String[] datepatterns, final boolean oneHeader) {
        this.compatibilityLevel = ((compatibilityLevel != null) ? compatibilityLevel : CompatibilityLevel.DEFAULT);
        this.publicSuffixMatcher = publicSuffixMatcher;
        this.datepatterns = datepatterns;
        this.oneHeader = oneHeader;
    }
    
    public DefaultCookieSpecProvider(final CompatibilityLevel compatibilityLevel, final PublicSuffixMatcher publicSuffixMatcher) {
        this(compatibilityLevel, publicSuffixMatcher, null, false);
    }
    
    public DefaultCookieSpecProvider(final PublicSuffixMatcher publicSuffixMatcher) {
        this(CompatibilityLevel.DEFAULT, publicSuffixMatcher, null, false);
    }
    
    public DefaultCookieSpecProvider() {
        this(CompatibilityLevel.DEFAULT, null, null, false);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    final RFC2965Spec strict = new RFC2965Spec(this.oneHeader, new CommonCookieAttributeHandler[] { new RFC2965VersionAttributeHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2965DomainAttributeHandler(), this.publicSuffixMatcher), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler() });
                    final RFC2109Spec obsoleteStrict = new RFC2109Spec(this.oneHeader, new CommonCookieAttributeHandler[] { new RFC2109VersionHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler() });
                    final NetscapeDraftSpec netscapeDraft = new NetscapeDraftSpec(new CommonCookieAttributeHandler[] { PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher), (this.compatibilityLevel == CompatibilityLevel.IE_MEDIUM_SECURITY) ? new BasicPathHandler() {
                            @Override
                            public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
                            }
                        } : new BasicPathHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((this.datepatterns != null) ? this.datepatterns.clone() : new String[] { "EEE, dd-MMM-yy HH:mm:ss z" }) });
                    this.cookieSpec = new DefaultCookieSpec(strict, obsoleteStrict, netscapeDraft);
                }
            }
        }
        return this.cookieSpec;
    }
    
    public enum CompatibilityLevel
    {
        DEFAULT, 
        IE_MEDIUM_SECURITY;
    }
}
