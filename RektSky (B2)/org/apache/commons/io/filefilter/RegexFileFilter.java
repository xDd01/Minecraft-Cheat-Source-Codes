package org.apache.commons.io.filefilter;

import java.util.regex.*;
import org.apache.commons.io.*;
import java.io.*;

public class RegexFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 4269646126155225062L;
    private final Pattern pattern;
    
    public RegexFileFilter(final String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        this.pattern = Pattern.compile(pattern);
    }
    
    public RegexFileFilter(final String pattern, final IOCase caseSensitivity) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        int flags = 0;
        if (caseSensitivity != null && !caseSensitivity.isCaseSensitive()) {
            flags = 2;
        }
        this.pattern = Pattern.compile(pattern, flags);
    }
    
    public RegexFileFilter(final String pattern, final int flags) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        this.pattern = Pattern.compile(pattern, flags);
    }
    
    public RegexFileFilter(final Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        this.pattern = pattern;
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        return this.pattern.matcher(name).matches();
    }
}
