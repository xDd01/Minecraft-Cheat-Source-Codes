package org.lwjgl.util;

public interface ReadablePoint
{
    int getX();
    
    int getY();
    
    void getLocation(final WritablePoint p0);
}
