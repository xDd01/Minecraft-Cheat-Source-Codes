// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.chunk;

import net.minecraft.util.BlockPos;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.World;

public class VboChunkFactory implements IRenderChunkFactory
{
    @Override
    public RenderChunk makeRenderChunk(final World worldIn, final RenderGlobal globalRenderer, final BlockPos pos, final int index) {
        return new RenderChunk(worldIn, globalRenderer, pos, index);
    }
}
