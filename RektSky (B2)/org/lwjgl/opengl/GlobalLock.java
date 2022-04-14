package org.lwjgl.opengl;

final class GlobalLock
{
    static final Object lock;
    
    static {
        lock = new Object();
    }
}
