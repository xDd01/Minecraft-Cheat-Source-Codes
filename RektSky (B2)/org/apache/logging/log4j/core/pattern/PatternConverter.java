package org.apache.logging.log4j.core.pattern;

public interface PatternConverter
{
    void format(final Object p0, final StringBuilder p1);
    
    String getName();
    
    String getStyleClass(final Object p0);
}
