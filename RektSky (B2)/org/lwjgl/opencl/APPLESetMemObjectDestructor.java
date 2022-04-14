package org.lwjgl.opencl;

import org.lwjgl.*;

public final class APPLESetMemObjectDestructor
{
    private APPLESetMemObjectDestructor() {
    }
    
    public static int clSetMemObjectDestructorAPPLE(final CLMem memobj, final CLMemObjectDestructorCallback pfn_notify) {
        final long function_pointer = CLCapabilities.clSetMemObjectDestructorAPPLE;
        BufferChecks.checkFunctionAddress(function_pointer);
        final long user_data = CallbackUtil.createGlobalRef(pfn_notify);
        int __result = 0;
        try {
            __result = nclSetMemObjectDestructorAPPLE(memobj.getPointer(), pfn_notify.getPointer(), user_data, function_pointer);
            return __result;
        }
        finally {
            CallbackUtil.checkCallback(__result, user_data);
        }
    }
    
    static native int nclSetMemObjectDestructorAPPLE(final long p0, final long p1, final long p2, final long p3);
}
