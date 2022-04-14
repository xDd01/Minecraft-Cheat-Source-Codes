/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ScaffoldUtil {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private static final List<Block> INVALID_PLACING_BLOCKS = Arrays.asList(Blocks.air, Blocks.water, Blocks.chest, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.red_flower, Blocks.double_plant, Blocks.yellow_flower, Blocks.bed, Blocks.stone_slab, Blocks.wooden_slab, Blocks.heavy_weighted_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.stone_slab2, Blocks.tripwire, Blocks.tripwire_hook, Blocks.tallgrass, Blocks.dispenser, Blocks.command_block);
    private static final List<Block> INVALID_BLOCKS = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.sand, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.red_flower, Blocks.double_plant, Blocks.yellow_flower, Blocks.bed, Blocks.ladder, Blocks.waterlily, Blocks.heavy_weighted_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.tripwire, Blocks.tripwire_hook, Blocks.tallgrass, Blocks.dispenser, Blocks.command_block, Blocks.web);

    public static BlockInfo getBlockInfo(BlockPos blockPos) {
        blockPos = blockPos.down();
        BlockInfo blockInfo = null;
        for (EnumFacing facing : EnumFacing.values()) {
            Block block = ScaffoldUtil.MINECRAFT.theWorld.getBlockState(blockPos.offset(facing)).getBlock();
            if (INVALID_PLACING_BLOCKS.contains(block)) continue;
            blockInfo = new BlockInfo(block, blockPos.offset(facing), facing.getOpposite());
        }
        return blockInfo;
    }

    private static boolean isBlockValid(Block block) {
        return !INVALID_BLOCKS.contains(block);
    }

    public static boolean isStackValid(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) {
            return false;
        }
        return ScaffoldUtil.isBlockValid(((ItemBlock)itemStack.getItem()).getBlock());
    }

    public static int getSlot() {
        for (int i2 = 36; i2 < 45; ++i2) {
            ItemStack stack = ScaffoldUtil.MINECRAFT.thePlayer.inventoryContainer.getSlot(i2).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemBlock) || stack.stackSize <= 0 || !ScaffoldUtil.isBlockValid(((ItemBlock)stack.getItem()).getBlock())) continue;
            return i2 - 36;
        }
        return -1;
    }

    public static List<Block> getInvalidBlocks() {
        return INVALID_BLOCKS;
    }

    public static boolean invCheck() {
        for (int i2 = 36; i2 < 45; ++i2) {
            if (!ScaffoldUtil.MINECRAFT.thePlayer.inventoryContainer.getSlot(i2).getHasStack() || !ScaffoldUtil.isStackValid(ScaffoldUtil.MINECRAFT.thePlayer.inventoryContainer.getSlot(i2).getStack())) continue;
            return false;
        }
        return true;
    }

    public static class BlockInfo {
        private final Block block;
        private final BlockPos blockPos;
        private final EnumFacing direction;

        public BlockInfo(Block block, BlockPos blockPos, EnumFacing direction) {
            this.block = block;
            this.blockPos = blockPos;
            this.direction = direction;
        }

        public Block getBlock() {
            return this.block;
        }

        public BlockPos getBlockPos() {
            return this.blockPos;
        }

        public EnumFacing getDirection() {
            return this.direction;
        }
    }
}

