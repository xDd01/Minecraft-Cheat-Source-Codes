package org.apache.logging.log4j.core.appender.db.jpa.converter;

import java.util.*;
import javax.persistence.*;

@Converter(autoApply = false)
public class ContextMapAttributeConverter implements AttributeConverter<Map<String, String>, String>
{
    public String convertToDatabaseColumn(final Map<String, String> contextMap) {
        if (contextMap == null) {
            return null;
        }
        return contextMap.toString();
    }
    
    public Map<String, String> convertToEntityAttribute(final String s) {
        throw new UnsupportedOperationException("Log events can only be persisted, not extracted.");
    }
}
