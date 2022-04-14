package org.newdawn.slick.util;

public interface Pauseable
{
    void pauseUpdate();
    
    void pauseRender();
    
    void unpauseUpdate();
    
    void unpauseRender();
    
    boolean isUpdatePaused();
    
    boolean isRenderPaused();
    
    void setUpdatePaused(final boolean p0);
    
    void setRenderPaused(final boolean p0);
}
