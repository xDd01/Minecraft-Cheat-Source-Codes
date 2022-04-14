package org.lwjgl.util;

public interface ReadableRectangle extends ReadableDimension, ReadablePoint
{
    void getBounds(final WritableRectangle p0);
}
