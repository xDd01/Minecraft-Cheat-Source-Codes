package org.lwjgl.opengl;

import org.lwjgl.*;

public interface Drawable
{
    boolean isCurrent() throws LWJGLException;
    
    void makeCurrent() throws LWJGLException;
    
    void releaseContext() throws LWJGLException;
    
    void destroy();
    
    void setCLSharingProperties(final PointerBuffer p0) throws LWJGLException;
}
