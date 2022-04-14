package org.lwjgl.openal;

import java.util.*;

public final class ALCdevice
{
    final long device;
    private boolean valid;
    private final HashMap<Long, ALCcontext> contexts;
    
    ALCdevice(final long device) {
        this.contexts = new HashMap<Long, ALCcontext>();
        this.device = device;
        this.valid = true;
    }
    
    @Override
    public boolean equals(final Object device) {
        if (device instanceof ALCdevice) {
            return ((ALCdevice)device).device == this.device;
        }
        return super.equals(device);
    }
    
    void addContext(final ALCcontext context) {
        synchronized (this.contexts) {
            this.contexts.put(context.context, context);
        }
    }
    
    void removeContext(final ALCcontext context) {
        synchronized (this.contexts) {
            this.contexts.remove(context.context);
        }
    }
    
    void setInvalid() {
        this.valid = false;
        synchronized (this.contexts) {
            for (final ALCcontext context : this.contexts.values()) {
                context.setInvalid();
            }
        }
        this.contexts.clear();
    }
    
    public boolean isValid() {
        return this.valid;
    }
}
