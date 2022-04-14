package io.netty.handler.ssl;

import io.netty.util.internal.logging.*;
import io.netty.util.internal.*;
import org.apache.tomcat.jni.*;

public final class OpenSsl
{
    private static final InternalLogger logger;
    private static final Throwable UNAVAILABILITY_CAUSE;
    static final String IGNORABLE_ERROR_PREFIX = "error:00000000:";
    
    public static boolean isAvailable() {
        return OpenSsl.UNAVAILABILITY_CAUSE == null;
    }
    
    public static void ensureAvailability() {
        if (OpenSsl.UNAVAILABILITY_CAUSE != null) {
            throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(OpenSsl.UNAVAILABILITY_CAUSE);
        }
    }
    
    public static Throwable unavailabilityCause() {
        return OpenSsl.UNAVAILABILITY_CAUSE;
    }
    
    private OpenSsl() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(OpenSsl.class);
        Throwable cause = null;
        try {
            NativeLibraryLoader.load("netty-tcnative", SSL.class.getClassLoader());
            Library.initialize("provided");
            SSL.initialize((String)null);
        }
        catch (Throwable t) {
            cause = t;
            OpenSsl.logger.debug("Failed to load netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.", t);
        }
        UNAVAILABILITY_CAUSE = cause;
    }
}
