package org.lwjgl.util.vector;

import java.nio.*;

public interface ReadableVector
{
    float length();
    
    float lengthSquared();
    
    Vector store(final FloatBuffer p0);
}
