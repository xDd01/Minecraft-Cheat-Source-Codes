package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.*;

@Plugin(name = "FileLocationPatternConverter", category = "Converter")
@ConverterKeys({ "F", "file" })
public final class FileLocationPatternConverter extends LogEventPatternConverter
{
    private static final FileLocationPatternConverter INSTANCE;
    
    private FileLocationPatternConverter() {
        super("File Location", "file");
    }
    
    public static FileLocationPatternConverter newInstance(final String[] options) {
        return FileLocationPatternConverter.INSTANCE;
    }
    
    @Override
    public void format(final LogEvent event, final StringBuilder output) {
        final StackTraceElement element = event.getSource();
        if (element != null) {
            output.append(element.getFileName());
        }
    }
    
    static {
        INSTANCE = new FileLocationPatternConverter();
    }
}
