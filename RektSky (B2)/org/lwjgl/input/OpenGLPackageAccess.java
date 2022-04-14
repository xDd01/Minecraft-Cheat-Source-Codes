package org.lwjgl.input;

import org.lwjgl.opengl.*;
import java.security.*;
import java.lang.reflect.*;

final class OpenGLPackageAccess
{
    static final Object global_lock;
    
    static InputImplementation createImplementation() {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputImplementation>)new PrivilegedExceptionAction<InputImplementation>() {
                public InputImplementation run() throws Exception {
                    final Method getImplementation_method = Display.class.getDeclaredMethod("getImplementation", (Class<?>[])new Class[0]);
                    getImplementation_method.setAccessible(true);
                    return (InputImplementation)getImplementation_method.invoke(null, new Object[0]);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw new Error(e);
        }
    }
    
    static {
        try {
            global_lock = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    final Field lock_field = Class.forName("org.lwjgl.opengl.GlobalLock").getDeclaredField("lock");
                    lock_field.setAccessible(true);
                    return lock_field.get(null);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw new Error(e);
        }
    }
}
