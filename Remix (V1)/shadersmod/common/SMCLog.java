package shadersmod.common;

import java.util.*;
import java.util.logging.*;
import java.io.*;

public abstract class SMCLog
{
    public static final String smcLogName = "SMC";
    public static final Logger logger;
    public static final Level SMCINFO;
    public static final Level SMCCONFIG;
    public static final Level SMCFINE;
    public static final Level SMCFINER;
    public static final Level SMCFINEST;
    
    public static void log(final Level level, final String message) {
        if (SMCLog.logger.isLoggable(level)) {
            SMCLog.logger.log(level, message);
        }
    }
    
    public static void severe(final String message) {
        if (SMCLog.logger.isLoggable(Level.SEVERE)) {
            SMCLog.logger.log(Level.SEVERE, message);
        }
    }
    
    public static void warning(final String message) {
        if (SMCLog.logger.isLoggable(Level.WARNING)) {
            SMCLog.logger.log(Level.WARNING, message);
        }
    }
    
    public static void info(final String message) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCINFO)) {
            SMCLog.logger.log(SMCLog.SMCINFO, message);
        }
    }
    
    public static void config(final String message) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCCONFIG)) {
            SMCLog.logger.log(SMCLog.SMCCONFIG, message);
        }
    }
    
    public static void fine(final String message) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCFINE)) {
            SMCLog.logger.log(SMCLog.SMCFINE, message);
        }
    }
    
    public static void finer(final String message) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCFINER)) {
            SMCLog.logger.log(SMCLog.SMCFINER, message);
        }
    }
    
    public static void finest(final String message) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCFINEST)) {
            SMCLog.logger.log(SMCLog.SMCFINEST, message);
        }
    }
    
    public static void log(final Level level, final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(level)) {
            SMCLog.logger.log(level, String.format(format, args));
        }
    }
    
    public static void severe(final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(Level.SEVERE)) {
            SMCLog.logger.log(Level.SEVERE, String.format(format, args));
        }
    }
    
    public static void warning(final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(Level.WARNING)) {
            SMCLog.logger.log(Level.WARNING, String.format(format, args));
        }
    }
    
    public static void info(final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCINFO)) {
            SMCLog.logger.log(SMCLog.SMCINFO, String.format(format, args));
        }
    }
    
    public static void config(final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCCONFIG)) {
            SMCLog.logger.log(SMCLog.SMCCONFIG, String.format(format, args));
        }
    }
    
    public static void fine(final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCFINE)) {
            SMCLog.logger.log(SMCLog.SMCFINE, String.format(format, args));
        }
    }
    
    public static void finer(final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCFINER)) {
            SMCLog.logger.log(SMCLog.SMCFINER, String.format(format, args));
        }
    }
    
    public static void finest(final String format, final Object... args) {
        if (SMCLog.logger.isLoggable(SMCLog.SMCFINEST)) {
            SMCLog.logger.log(SMCLog.SMCFINEST, String.format(format, args));
        }
    }
    
    static {
        logger = new SMCLogger("SMC");
        SMCINFO = new SMCLevel("INF", 850, (NamelessClass763038833)null);
        SMCCONFIG = new SMCLevel("CFG", 840, (NamelessClass763038833)null);
        SMCFINE = new SMCLevel("FNE", 830, (NamelessClass763038833)null);
        SMCFINER = new SMCLevel("FNR", 820, (NamelessClass763038833)null);
        SMCFINEST = new SMCLevel("FNT", 810, (NamelessClass763038833)null);
    }
    
    static class NamelessClass763038833
    {
    }
    
    private static class SMCFormatter extends Formatter
    {
        int tzOffset;
        
        private SMCFormatter() {
            this.tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
        }
        
        @Override
        public String format(final LogRecord record) {
            final StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("Shaders").append("]");
            if (record.getLevel() != SMCLog.SMCINFO) {
                sb.append("[").append(record.getLevel()).append("]");
            }
            sb.append(" ");
            sb.append(record.getMessage()).append("\n");
            return sb.toString();
        }
    }
    
    private static class SMCLevel extends Level
    {
        private SMCLevel(final String name, final int value) {
            super(name, value);
        }
        
        SMCLevel(final String x0, final int x1, final NamelessClass763038833 x2) {
            this(x0, x1);
        }
    }
    
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
}
