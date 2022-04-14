package shadersmod.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public abstract class SMCLog {
   public static final Level SMCFINEST = new SMCLog.SMCLevel("FNT", 810, (SMCLog.NamelessClass763038833)null);
   public static final Level SMCFINE = new SMCLog.SMCLevel("FNE", 830, (SMCLog.NamelessClass763038833)null);
   public static final String smcLogName = "SMC";
   public static final Logger logger = new SMCLog.SMCLogger("SMC");
   public static final Level SMCFINER = new SMCLog.SMCLevel("FNR", 820, (SMCLog.NamelessClass763038833)null);
   public static final Level SMCINFO = new SMCLog.SMCLevel("INF", 850, (SMCLog.NamelessClass763038833)null);
   public static final Level SMCCONFIG = new SMCLog.SMCLevel("CFG", 840, (SMCLog.NamelessClass763038833)null);

   public static void severe(String var0) {
      if (logger.isLoggable(Level.SEVERE)) {
         logger.log(Level.SEVERE, var0);
      }

   }

   public static void config(String var0, Object... var1) {
      if (logger.isLoggable(SMCCONFIG)) {
         logger.log(SMCCONFIG, String.format(var0, var1));
      }

   }

   public static void warning(String var0, Object... var1) {
      if (logger.isLoggable(Level.WARNING)) {
         logger.log(Level.WARNING, String.format(var0, var1));
      }

   }

   public static void log(Level var0, String var1, Object... var2) {
      if (logger.isLoggable(var0)) {
         logger.log(var0, String.format(var1, var2));
      }

   }

   public static void finest(String var0) {
      if (logger.isLoggable(SMCFINEST)) {
         logger.log(SMCFINEST, var0);
      }

   }

   public static void config(String var0) {
      if (logger.isLoggable(SMCCONFIG)) {
         logger.log(SMCCONFIG, var0);
      }

   }

   public static void warning(String var0) {
      if (logger.isLoggable(Level.WARNING)) {
         logger.log(Level.WARNING, var0);
      }

   }

   public static void severe(String var0, Object... var1) {
      if (logger.isLoggable(Level.SEVERE)) {
         logger.log(Level.SEVERE, String.format(var0, var1));
      }

   }

   public static void finest(String var0, Object... var1) {
      if (logger.isLoggable(SMCFINEST)) {
         logger.log(SMCFINEST, String.format(var0, var1));
      }

   }

   public static void fine(String var0, Object... var1) {
      if (logger.isLoggable(SMCFINE)) {
         logger.log(SMCFINE, String.format(var0, var1));
      }

   }

   public static void fine(String var0) {
      if (logger.isLoggable(SMCFINE)) {
         logger.log(SMCFINE, var0);
      }

   }

   public static void finer(String var0) {
      if (logger.isLoggable(SMCFINER)) {
         logger.log(SMCFINER, var0);
      }

   }

   public static void log(Level var0, String var1) {
      if (logger.isLoggable(var0)) {
         logger.log(var0, var1);
      }

   }

   public static void info(String var0, Object... var1) {
      if (logger.isLoggable(SMCINFO)) {
         logger.log(SMCINFO, String.format(var0, var1));
      }

   }

   public static void info(String var0) {
      if (logger.isLoggable(SMCINFO)) {
         logger.log(SMCINFO, var0);
      }

   }

   public static void finer(String var0, Object... var1) {
      if (logger.isLoggable(SMCFINER)) {
         logger.log(SMCFINER, String.format(var0, var1));
      }

   }

   static class NamelessClass763038833 {
   }

   private static class SMCLevel extends Level {
      private SMCLevel(String var1, int var2) {
         super(var1, var2);
      }

      SMCLevel(String var1, int var2, SMCLog.NamelessClass763038833 var3) {
         this(var1, var2);
      }
   }

   private static class SMCFormatter extends Formatter {
      int tzOffset;

      public String format(LogRecord var1) {
         StringBuilder var2 = new StringBuilder();
         var2.append("[");
         var2.append("Shaders").append("]");
         if (var1.getLevel() != SMCLog.SMCINFO) {
            var2.append("[").append(var1.getLevel()).append("]");
         }

         var2.append(" ");
         var2.append(var1.getMessage()).append("\n");
         return String.valueOf(var2);
      }

      SMCFormatter(SMCLog.SMCFormatter var1) {
         this();
      }

      private SMCFormatter() {
         this.tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
      }
   }

   private static class SMCLogger extends Logger {
      SMCLogger(String var1) {
         super(var1, (String)null);
         this.setUseParentHandlers(false);
         SMCLog.SMCFormatter var2 = new SMCLog.SMCFormatter((SMCLog.SMCFormatter)null);
         ConsoleHandler var3 = new ConsoleHandler();
         var3.setFormatter(var2);
         var3.setLevel(Level.ALL);
         this.addHandler(var3);

         try {
            FileOutputStream var4 = new FileOutputStream("logs/shadersmod.log", false);
            StreamHandler var5 = new StreamHandler(this, var4, var2) {
               final SMCLog.SMCLogger this$1;

               {
                  this.this$1 = var1;
               }

               public synchronized void publish(LogRecord var1) {
                  super.publish(var1);
                  this.flush();
               }
            };
            var5.setFormatter(var2);
            var5.setLevel(Level.ALL);
            this.addHandler(var5);
         } catch (IOException var6) {
            var6.printStackTrace();
         }

         this.setLevel(Level.ALL);
      }
   }
}
