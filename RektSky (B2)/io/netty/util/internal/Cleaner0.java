package io.netty.util.internal;

import java.nio.*;
import sun.misc.*;
import io.netty.util.internal.logging.*;
import java.lang.reflect.*;

final class Cleaner0
{
    private static final long CLEANER_FIELD_OFFSET;
    private static final InternalLogger logger;
    
    static void freeDirectBuffer(final ByteBuffer buffer) {
        if (Cleaner0.CLEANER_FIELD_OFFSET == -1L || !buffer.isDirect()) {
            return;
        }
        try {
            final Cleaner cleaner = (Cleaner)PlatformDependent0.getObject(buffer, Cleaner0.CLEANER_FIELD_OFFSET);
            if (cleaner != null) {
                cleaner.clean();
            }
        }
        catch (Throwable t) {}
    }
    
    private Cleaner0() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Cleaner0.class);
        final ByteBuffer direct = ByteBuffer.allocateDirect(1);
        long fieldOffset = -1L;
        if (PlatformDependent0.hasUnsafe()) {
            try {
                final Field cleanerField = direct.getClass().getDeclaredField("cleaner");
                cleanerField.setAccessible(true);
                final Cleaner cleaner = (Cleaner)cleanerField.get(direct);
                cleaner.clean();
                fieldOffset = PlatformDependent0.objectFieldOffset(cleanerField);
            }
            catch (Throwable t) {
                fieldOffset = -1L;
            }
        }
        Cleaner0.logger.debug("java.nio.ByteBuffer.cleaner(): {}", (fieldOffset != -1L) ? "available" : "unavailable");
        CLEANER_FIELD_OFFSET = fieldOffset;
        freeDirectBuffer(direct);
    }
}
