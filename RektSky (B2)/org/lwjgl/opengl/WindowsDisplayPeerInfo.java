package org.lwjgl.opengl;

import org.lwjgl.opengles.*;
import org.lwjgl.*;
import java.nio.*;

final class WindowsDisplayPeerInfo extends WindowsPeerInfo
{
    final boolean egl;
    
    WindowsDisplayPeerInfo(final boolean egl) throws LWJGLException {
        this.egl = egl;
        if (egl) {
            GLContext.loadOpenGLLibrary();
        }
        else {
            org.lwjgl.opengl.GLContext.loadOpenGLLibrary();
        }
    }
    
    void initDC(final long hwnd, final long hdc) throws LWJGLException {
        nInitDC(this.getHandle(), hwnd, hdc);
    }
    
    private static native void nInitDC(final ByteBuffer p0, final long p1, final long p2);
    
    @Override
    protected void doLockAndInitHandle() throws LWJGLException {
    }
    
    @Override
    protected void doUnlock() throws LWJGLException {
    }
    
    @Override
    public void destroy() {
        super.destroy();
        if (this.egl) {
            GLContext.unloadOpenGLLibrary();
        }
        else {
            org.lwjgl.opengl.GLContext.unloadOpenGLLibrary();
        }
    }
}
