package org.apache.commons.io.serialization;

import org.apache.commons.io.*;

final class WildcardClassNameMatcher implements ClassNameMatcher
{
    private final String pattern;
    
    public WildcardClassNameMatcher(final String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public boolean matches(final String className) {
        return FilenameUtils.wildcardMatch(className, this.pattern);
    }
}
