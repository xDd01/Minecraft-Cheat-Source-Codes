package org.apache.logging.log4j.core.net;

import javax.naming.*;
import javax.jms.*;
import java.nio.charset.*;
import java.io.*;

public class JMSQueueReceiver extends AbstractJMSReceiver
{
    public JMSQueueReceiver(final String qcfBindingName, final String queueBindingName, final String username, final String password) {
        try {
            final Context ctx = new InitialContext();
            final QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory)this.lookup(ctx, qcfBindingName);
            final QueueConnection queueConnection = queueConnectionFactory.createQueueConnection(username, password);
            queueConnection.start();
            final QueueSession queueSession = queueConnection.createQueueSession(false, 1);
            final Queue queue = (Queue)ctx.lookup(queueBindingName);
            final QueueReceiver queueReceiver = queueSession.createReceiver(queue);
            queueReceiver.setMessageListener((MessageListener)this);
        }
        catch (JMSException e) {
            this.logger.error("Could not read JMS message.", (Throwable)e);
        }
        catch (NamingException e2) {
            this.logger.error("Could not read JMS message.", e2);
        }
        catch (RuntimeException e3) {
            this.logger.error("Could not read JMS message.", e3);
        }
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 4) {
            usage("Wrong number of arguments.");
        }
        final String qcfBindingName = args[0];
        final String queueBindingName = args[1];
        final String username = args[2];
        final String password = args[3];
        new JMSQueueReceiver(qcfBindingName, queueBindingName, username, password);
        final Charset enc = Charset.defaultCharset();
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, enc));
        System.out.println("Type \"exit\" to quit JMSQueueReceiver.");
        String line;
        do {
            line = stdin.readLine();
        } while (line != null && !line.equalsIgnoreCase("exit"));
        System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
    }
    
    private static void usage(final String msg) {
        System.err.println(msg);
        System.err.println("Usage: java " + JMSQueueReceiver.class.getName() + " QueueConnectionFactoryBindingName QueueBindingName username password");
        System.exit(1);
    }
}
