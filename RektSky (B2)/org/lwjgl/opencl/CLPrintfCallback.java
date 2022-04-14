package org.lwjgl.opencl;

import org.lwjgl.*;

public abstract class CLPrintfCallback extends PointerWrapperAbstract
{
    protected CLPrintfCallback() {
        super(CallbackUtil.getPrintfCallback());
    }
    
    protected abstract void handleMessage(final String p0);
}
