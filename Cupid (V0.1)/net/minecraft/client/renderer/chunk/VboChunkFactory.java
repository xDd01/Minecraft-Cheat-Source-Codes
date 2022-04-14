package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class VboChunkFactory implements IRenderChunkFactory {
  public RenderChunk makeRenderChunk(World worldIn, RenderGlobal globalRenderer, BlockPos pos, int index) {
    return new RenderChunk(worldIn, globalRenderer, pos, index);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\net\minecraft\client\renderer\chunk\VboChunkFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */