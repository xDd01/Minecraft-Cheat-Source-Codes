package org.apache.logging.log4j.core.appender.db.jpa.converter;

import org.apache.logging.log4j.message.*;
import javax.persistence.*;
import org.apache.logging.log4j.status.*;
import org.apache.logging.log4j.core.helpers.*;

@Converter(autoApply = false)
public class MessageAttributeConverter implements AttributeConverter<Message, String>
{
    private static final StatusLogger LOGGER;
    
    public String convertToDatabaseColumn(final Message message) {
        if (message == null) {
            return null;
        }
        return message.getFormattedMessage();
    }
    
    public Message convertToEntityAttribute(final String s) {
        if (Strings.isEmpty(s)) {
            return null;
        }
        return MessageAttributeConverter.LOGGER.getMessageFactory().newMessage(s);
    }
    
    static {
        LOGGER = StatusLogger.getLogger();
    }
}
