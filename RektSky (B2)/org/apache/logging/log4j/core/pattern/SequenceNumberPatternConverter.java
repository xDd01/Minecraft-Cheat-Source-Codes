package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.*;
import java.util.concurrent.atomic.*;
import org.apache.logging.log4j.core.*;

@Plugin(name = "SequenceNumberPatternConverter", category = "Converter")
@ConverterKeys({ "sn", "sequenceNumber" })
public final class SequenceNumberPatternConverter extends LogEventPatternConverter
{
    private static final AtomicLong SEQUENCE;
    private static final SequenceNumberPatternConverter INSTANCE;
    
    private SequenceNumberPatternConverter() {
        super("Sequence Number", "sn");
    }
    
    public static SequenceNumberPatternConverter newInstance(final String[] options) {
        return SequenceNumberPatternConverter.INSTANCE;
    }
    
    @Override
    public void format(final LogEvent event, final StringBuilder toAppendTo) {
        toAppendTo.append(Long.toString(SequenceNumberPatternConverter.SEQUENCE.incrementAndGet()));
    }
    
    static {
        SEQUENCE = new AtomicLong();
        INSTANCE = new SequenceNumberPatternConverter();
    }
}
