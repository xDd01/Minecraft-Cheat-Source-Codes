package org.lwjgl.opencl;

import org.lwjgl.*;

abstract class CLProgramCallback extends PointerWrapperAbstract
{
    private CLContext context;
    
    protected CLProgramCallback() {
        super(CallbackUtil.getProgramCallback());
    }
    
    final void setContext(final CLContext context) {
        this.context = context;
    }
    
    private void handleMessage(final long program_address) {
        this.handleMessage(this.context.getCLProgram(program_address));
    }
    
    protected abstract void handleMessage(final CLProgram p0);
}
