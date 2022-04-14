package org.lwjgl.opengl;

import java.nio.*;
import org.lwjgl.*;

public final class GREMEDYStringMarker
{
    private GREMEDYStringMarker() {
    }
    
    public static void glStringMarkerGREMEDY(final ByteBuffer string) {
        final ContextCapabilities caps = GLContext.getCapabilities();
        final long function_pointer = caps.glStringMarkerGREMEDY;
        BufferChecks.checkFunctionAddress(function_pointer);
        BufferChecks.checkDirect(string);
        nglStringMarkerGREMEDY(string.remaining(), MemoryUtil.getAddress(string), function_pointer);
    }
    
    static native void nglStringMarkerGREMEDY(final int p0, final long p1, final long p2);
    
    public static void glStringMarkerGREMEDY(final CharSequence string) {
        final ContextCapabilities caps = GLContext.getCapabilities();
        final long function_pointer = caps.glStringMarkerGREMEDY;
        BufferChecks.checkFunctionAddress(function_pointer);
        nglStringMarkerGREMEDY(string.length(), APIUtil.getBuffer(caps, string), function_pointer);
    }
}
