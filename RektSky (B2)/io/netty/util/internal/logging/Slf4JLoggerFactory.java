package io.netty.util.internal.logging;

import java.io.*;
import org.slf4j.helpers.*;
import org.slf4j.*;

public class Slf4JLoggerFactory extends InternalLoggerFactory
{
    public Slf4JLoggerFactory() {
    }
    
    Slf4JLoggerFactory(final boolean failIfNOP) {
        assert failIfNOP;
        final StringBuffer buf = new StringBuffer();
        final PrintStream err = System.err;
        try {
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(final int b) {
                    buf.append((char)b);
                }
            }, true, "US-ASCII"));
        }
        catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        try {
            if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
                throw new NoClassDefFoundError(buf.toString());
            }
            err.print(buf);
            err.flush();
        }
        finally {
            System.setErr(err);
        }
    }
    
    public InternalLogger newInstance(final String name) {
        return new Slf4JLogger(LoggerFactory.getLogger(name));
    }
}
