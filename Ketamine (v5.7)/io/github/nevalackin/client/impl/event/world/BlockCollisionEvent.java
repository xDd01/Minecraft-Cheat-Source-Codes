package io.github.nevalackin.client.impl.event.world;

import io.github.nevalackin.client.api.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public final class BlockCollisionEvent implements Event {

    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;

    public BlockCollisionEvent(final Block block, final BlockPos pos, final AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
    }

    public final Block getBlock() {
        return block;
    }

    public final BlockPos getBlockPos() {
        return blockPos;
    }

    public final AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public final void setBoundingBox(final AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}