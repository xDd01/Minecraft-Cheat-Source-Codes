// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager
{
    void reloadResources(final List<IResourcePack> p0);
    
    void registerReloadListener(final IResourceManagerReloadListener p0);
}
