package org.lwjgl.opencl;

import org.lwjgl.*;

public abstract class CLMemObjectDestructorCallback extends PointerWrapperAbstract
{
    protected CLMemObjectDestructorCallback() {
        super(CallbackUtil.getMemObjectDestructorCallback());
    }
    
    protected abstract void handleMessage(final long p0);
}
