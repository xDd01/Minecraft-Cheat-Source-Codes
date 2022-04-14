package org.lwjgl.opencl;

import org.lwjgl.*;
import java.nio.*;

public abstract class CLNativeKernel extends PointerWrapperAbstract
{
    protected CLNativeKernel() {
        super(CallbackUtil.getNativeKernelCallback());
    }
    
    protected abstract void execute(final ByteBuffer[] p0);
}
