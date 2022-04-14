package org.apache.logging.log4j.core.net;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import javax.jms.*;
import javax.naming.*;

public abstract class AbstractJMSReceiver extends AbstractServer implements MessageListener
{
    protected Logger logger;
    
    public AbstractJMSReceiver() {
        this.logger = LogManager.getLogger(this.getClass().getName());
    }
    
    public void onMessage(final Message message) {
        try {
            if (message instanceof ObjectMessage) {
                final ObjectMessage objectMessage = (ObjectMessage)message;
                this.log((LogEvent)objectMessage.getObject());
            }
            else {
                this.logger.warn("Received message is of type " + message.getJMSType() + ", was expecting ObjectMessage.");
            }
        }
        catch (JMSException jmse) {
            this.logger.error("Exception thrown while processing incoming message.", (Throwable)jmse);
        }
    }
    
    protected Object lookup(final Context ctx, final String name) throws NamingException {
        try {
            return ctx.lookup(name);
        }
        catch (NameNotFoundException e) {
            this.logger.error("Could not find name [" + name + "].");
            throw e;
        }
    }
}
