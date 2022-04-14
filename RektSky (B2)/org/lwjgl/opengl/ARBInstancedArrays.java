package org.lwjgl.opengl;

import org.lwjgl.*;

public final class ARBInstancedArrays
{
    public static final int GL_VERTEX_ATTRIB_ARRAY_DIVISOR_ARB = 35070;
    
    private ARBInstancedArrays() {
    }
    
    public static void glVertexAttribDivisorARB(final int index, final int divisor) {
        final ContextCapabilities caps = GLContext.getCapabilities();
        final long function_pointer = caps.glVertexAttribDivisorARB;
        BufferChecks.checkFunctionAddress(function_pointer);
        nglVertexAttribDivisorARB(index, divisor, function_pointer);
    }
    
    static native void nglVertexAttribDivisorARB(final int p0, final int p1, final long p2);
}
