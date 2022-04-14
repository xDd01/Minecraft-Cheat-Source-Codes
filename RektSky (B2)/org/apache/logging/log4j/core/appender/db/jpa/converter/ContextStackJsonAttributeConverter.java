package org.apache.logging.log4j.core.appender.db.jpa.converter;

import org.apache.logging.log4j.*;
import javax.persistence.*;
import java.io.*;
import org.apache.logging.log4j.core.helpers.*;
import com.fasterxml.jackson.core.type.*;
import org.apache.logging.log4j.spi.*;
import java.util.*;

@Converter(autoApply = false)
public class ContextStackJsonAttributeConverter implements AttributeConverter<ThreadContext.ContextStack, String>
{
    public String convertToDatabaseColumn(final ThreadContext.ContextStack contextStack) {
        if (contextStack == null) {
            return null;
        }
        try {
            return ContextMapJsonAttributeConverter.OBJECT_MAPPER.writeValueAsString((Object)contextStack.asList());
        }
        catch (IOException e) {
            throw new PersistenceException("Failed to convert stack list to JSON string.", (Throwable)e);
        }
    }
    
    public ThreadContext.ContextStack convertToEntityAttribute(final String s) {
        if (Strings.isEmpty(s)) {
            return null;
        }
        List<String> list;
        try {
            list = (List<String>)ContextMapJsonAttributeConverter.OBJECT_MAPPER.readValue(s, (TypeReference)new TypeReference<List<String>>() {});
        }
        catch (IOException e) {
            throw new PersistenceException("Failed to convert JSON string to list for stack.", (Throwable)e);
        }
        final DefaultThreadContextStack result = new DefaultThreadContextStack(true);
        result.addAll(list);
        return result;
    }
}
