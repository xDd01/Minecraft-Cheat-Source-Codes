package org.lwjgl.opengl;

import org.lwjgl.*;
import java.awt.*;

interface AWTCanvasImplementation
{
    PeerInfo createPeerInfo(final Canvas p0, final PixelFormat p1, final ContextAttribs p2) throws LWJGLException;
    
    GraphicsConfiguration findConfiguration(final GraphicsDevice p0, final PixelFormat p1) throws LWJGLException;
}
