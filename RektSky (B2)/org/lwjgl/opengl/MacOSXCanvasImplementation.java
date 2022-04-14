package org.lwjgl.opengl;

import org.lwjgl.*;
import java.awt.*;

final class MacOSXCanvasImplementation implements AWTCanvasImplementation
{
    public PeerInfo createPeerInfo(final Canvas component, final PixelFormat pixel_format, final ContextAttribs attribs) throws LWJGLException {
        try {
            return new MacOSXAWTGLCanvasPeerInfo(component, pixel_format, attribs, true);
        }
        catch (LWJGLException e) {
            return new MacOSXAWTGLCanvasPeerInfo(component, pixel_format, attribs, false);
        }
    }
    
    public GraphicsConfiguration findConfiguration(final GraphicsDevice device, final PixelFormat pixel_format) throws LWJGLException {
        return null;
    }
}
