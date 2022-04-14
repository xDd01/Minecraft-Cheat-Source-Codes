package org.lwjgl.util.vector;

public interface WritableVector4f extends WritableVector3f
{
    void setW(final float p0);
    
    void set(final float p0, final float p1, final float p2, final float p3);
}
