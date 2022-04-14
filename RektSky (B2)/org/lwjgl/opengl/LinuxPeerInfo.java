package org.lwjgl.opengl;

import java.nio.*;

abstract class LinuxPeerInfo extends PeerInfo
{
    LinuxPeerInfo() {
        super(createHandle());
    }
    
    private static native ByteBuffer createHandle();
    
    public final long getDisplay() {
        return nGetDisplay(this.getHandle());
    }
    
    private static native long nGetDisplay(final ByteBuffer p0);
    
    public final long getDrawable() {
        return nGetDrawable(this.getHandle());
    }
    
    private static native long nGetDrawable(final ByteBuffer p0);
}
