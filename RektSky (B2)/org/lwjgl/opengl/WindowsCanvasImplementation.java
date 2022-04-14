package org.lwjgl.opengl;

import java.awt.*;
import org.lwjgl.*;
import java.security.*;

final class WindowsCanvasImplementation implements AWTCanvasImplementation
{
    public PeerInfo createPeerInfo(final Canvas component, final PixelFormat pixel_format, final ContextAttribs attribs) throws LWJGLException {
        return new WindowsAWTGLCanvasPeerInfo(component, pixel_format);
    }
    
    public GraphicsConfiguration findConfiguration(final GraphicsDevice device, final PixelFormat pixel_format) throws LWJGLException {
        return null;
    }
    
    static {
        Toolkit.getDefaultToolkit();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    System.loadLibrary("jawt");
                }
                catch (UnsatisfiedLinkError e) {
                    LWJGLUtil.log("Failed to load jawt: " + e.getMessage());
                }
                return null;
            }
        });
    }
}
