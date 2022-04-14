package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.*;
import java.util.*;

@Plugin(name = "IntegerPatternConverter", category = "FileConverter")
@ConverterKeys({ "i", "index" })
public final class IntegerPatternConverter extends AbstractPatternConverter implements ArrayPatternConverter
{
    private static final IntegerPatternConverter INSTANCE;
    
    private IntegerPatternConverter() {
        super("Integer", "integer");
    }
    
    public static IntegerPatternConverter newInstance(final String[] options) {
        return IntegerPatternConverter.INSTANCE;
    }
    
    @Override
    public void format(final StringBuilder toAppendTo, final Object... objects) {
        for (final Object obj : objects) {
            if (obj instanceof Integer) {
                this.format(obj, toAppendTo);
                break;
            }
        }
    }
    
    @Override
    public void format(final Object obj, final StringBuilder toAppendTo) {
        if (obj instanceof Integer) {
            toAppendTo.append(obj.toString());
        }
        if (obj instanceof Date) {
            toAppendTo.append(Long.toString(((Date)obj).getTime()));
        }
    }
    
    static {
        INSTANCE = new IntegerPatternConverter();
    }
}
