package org.apache.logging.log4j.core.appender.db.jpa.converter;

import org.apache.logging.log4j.*;
import javax.persistence.*;
import java.util.*;

@Converter(autoApply = false)
public class ContextStackAttributeConverter implements AttributeConverter<ThreadContext.ContextStack, String>
{
    public String convertToDatabaseColumn(final ThreadContext.ContextStack contextStack) {
        if (contextStack == null) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        for (final String value : contextStack.asList()) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(value);
        }
        return builder.toString();
    }
    
    public ThreadContext.ContextStack convertToEntityAttribute(final String s) {
        throw new UnsupportedOperationException("Log events can only be persisted, not extracted.");
    }
}
