package org.apache.logging.log4j.message;

public interface MessageFactory
{
    Message newMessage(final Object p0);
    
    Message newMessage(final String p0);
    
    Message newMessage(final String p0, final Object... p1);
}
