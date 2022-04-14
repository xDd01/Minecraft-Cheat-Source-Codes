package org.apache.http.conn.util;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import java.util.concurrent.*;
import java.util.*;
import java.net.*;

@Contract(threading = ThreadingBehavior.SAFE)
public final class PublicSuffixMatcher
{
    private final Map<String, DomainType> rules;
    private final Map<String, DomainType> exceptions;
    
    public PublicSuffixMatcher(final Collection<String> rules, final Collection<String> exceptions) {
        this(DomainType.UNKNOWN, rules, exceptions);
    }
    
    public PublicSuffixMatcher(final DomainType domainType, final Collection<String> rules, final Collection<String> exceptions) {
        Args.notNull(domainType, "Domain type");
        Args.notNull(rules, "Domain suffix rules");
        this.rules = new ConcurrentHashMap<String, DomainType>(rules.size());
        for (final String rule : rules) {
            this.rules.put(rule, domainType);
        }
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        if (exceptions != null) {
            for (final String exception : exceptions) {
                this.exceptions.put(exception, domainType);
            }
        }
    }
    
    public PublicSuffixMatcher(final Collection<PublicSuffixList> lists) {
        Args.notNull(lists, "Domain suffix lists");
        this.rules = new ConcurrentHashMap<String, DomainType>();
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        for (final PublicSuffixList list : lists) {
            final DomainType domainType = list.getType();
            final List<String> rules = list.getRules();
            for (final String rule : rules) {
                this.rules.put(rule, domainType);
            }
            final List<String> exceptions = list.getExceptions();
            if (exceptions != null) {
                for (final String exception : exceptions) {
                    this.exceptions.put(exception, domainType);
                }
            }
        }
    }
    
    private static boolean hasEntry(final Map<String, DomainType> map, final String rule, final DomainType expectedType) {
        if (map == null) {
            return false;
        }
        final DomainType domainType = map.get(rule);
        return domainType != null && (expectedType == null || domainType.equals(expectedType));
    }
    
    private boolean hasRule(final String rule, final DomainType expectedType) {
        return hasEntry(this.rules, rule, expectedType);
    }
    
    private boolean hasException(final String exception, final DomainType expectedType) {
        return hasEntry(this.exceptions, exception, expectedType);
    }
    
    public String getDomainRoot(final String domain) {
        return this.getDomainRoot(domain, null);
    }
    
    public String getDomainRoot(final String domain, final DomainType expectedType) {
        if (domain == null) {
            return null;
        }
        if (domain.startsWith(".")) {
            return null;
        }
        String domainName = null;
        String nextSegment;
        for (String segment = domain.toLowerCase(Locale.ROOT); segment != null; segment = nextSegment) {
            if (this.hasException(IDN.toUnicode(segment), expectedType)) {
                return segment;
            }
            if (this.hasRule(IDN.toUnicode(segment), expectedType)) {
                break;
            }
            final int nextdot = segment.indexOf(46);
            nextSegment = ((nextdot != -1) ? segment.substring(nextdot + 1) : null);
            if (nextSegment != null && this.hasRule("*." + IDN.toUnicode(nextSegment), expectedType)) {
                break;
            }
            if (nextdot != -1) {
                domainName = segment;
            }
        }
        return domainName;
    }
    
    public boolean matches(final String domain) {
        return this.matches(domain, null);
    }
    
    public boolean matches(final String domain, final DomainType expectedType) {
        if (domain == null) {
            return false;
        }
        final String domainRoot = this.getDomainRoot(domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
        return domainRoot == null;
    }
}
