package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.*;

@Plugin(name = "LineLocationPatternConverter", category = "Converter")
@ConverterKeys({ "L", "line" })
public final class LineLocationPatternConverter extends LogEventPatternConverter
{
    private static final LineLocationPatternConverter INSTANCE;
    
    private LineLocationPatternConverter() {
        super("Line", "line");
    }
    
    public static LineLocationPatternConverter newInstance(final String[] options) {
        return LineLocationPatternConverter.INSTANCE;
    }
    
    @Override
    public void format(final LogEvent event, final StringBuilder output) {
        final StackTraceElement element = event.getSource();
        if (element != null) {
            output.append(element.getLineNumber());
        }
    }
    
    static {
        INSTANCE = new LineLocationPatternConverter();
    }
}
