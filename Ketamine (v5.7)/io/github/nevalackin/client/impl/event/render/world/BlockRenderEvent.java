package io.github.nevalackin.client.impl.event.render.world;

import io.github.nevalackin.client.api.event.CancellableEvent;
import net.minecraft.block.Block;

public final class BlockRenderEvent extends CancellableEvent {

    private final Block block;

    public BlockRenderEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
