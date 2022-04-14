package shadersmod.common;

import java.util.logging.*;
import java.io.*;

private static class SMCLogger extends Logger
{
    SMCLogger(final String name) {
        super(name, null);
        this.setUseParentHandlers(false);
        final SMCFormatter formatter = new SMCFormatter();
        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        handler.setLevel(Level.ALL);
        this.addHandler(handler);
        try {
            final FileOutputStream e = new FileOutputStream("logs/shadersmod.log", false);
            final StreamHandler handler2 = new StreamHandler(e, formatter) {
                @Override
                public synchronized void publish(final LogRecord record) {
                    super.publish(record);
                    this.flush();
                }
            };
            handler2.setFormatter(formatter);
            handler2.setLevel(Level.ALL);
            this.addHandler(handler2);
        }
        catch (IOException var5) {
            var5.printStackTrace();
        }
        this.setLevel(Level.ALL);
    }
}
