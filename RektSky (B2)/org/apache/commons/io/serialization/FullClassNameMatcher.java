package org.apache.commons.io.serialization;

import java.util.*;

final class FullClassNameMatcher implements ClassNameMatcher
{
    private final Set<String> classesSet;
    
    public FullClassNameMatcher(final String... classes) {
        this.classesSet = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList(classes)));
    }
    
    @Override
    public boolean matches(final String className) {
        return this.classesSet.contains(className);
    }
}
