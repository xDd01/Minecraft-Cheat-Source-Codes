package org.apache.http.conn.util;

import org.apache.http.annotation.*;
import org.apache.http.util.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class PublicSuffixList
{
    private final DomainType type;
    private final List<String> rules;
    private final List<String> exceptions;
    
    public PublicSuffixList(final DomainType type, final List<String> rules, final List<String> exceptions) {
        this.type = Args.notNull(type, "Domain type");
        this.rules = Collections.unmodifiableList((List<? extends String>)Args.notNull((List<? extends T>)rules, "Domain suffix rules"));
        this.exceptions = Collections.unmodifiableList((List<? extends String>)((exceptions != null) ? exceptions : Collections.emptyList()));
    }
    
    public PublicSuffixList(final List<String> rules, final List<String> exceptions) {
        this(DomainType.UNKNOWN, rules, exceptions);
    }
    
    public DomainType getType() {
        return this.type;
    }
    
    public List<String> getRules() {
        return this.rules;
    }
    
    public List<String> getExceptions() {
        return this.exceptions;
    }
}
