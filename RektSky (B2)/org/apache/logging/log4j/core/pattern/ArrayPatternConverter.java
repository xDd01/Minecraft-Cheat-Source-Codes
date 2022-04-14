package org.apache.logging.log4j.core.pattern;

public interface ArrayPatternConverter extends PatternConverter
{
    void format(final StringBuilder p0, final Object... p1);
}
