/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.biome.BiomeGenBase;
import optifine.MatchBlock;

public class Matches {
    public static boolean block(BlockStateBase p_block_0_, MatchBlock[] p_block_1_) {
        if (p_block_1_ == null) {
            return true;
        }
        for (int i2 = 0; i2 < p_block_1_.length; ++i2) {
            MatchBlock matchblock = p_block_1_[i2];
            if (!matchblock.matches(p_block_0_)) continue;
            return true;
        }
        return false;
    }

    public static boolean block(int p_block_0_, int p_block_1_, MatchBlock[] p_block_2_) {
        if (p_block_2_ == null) {
            return true;
        }
        for (int i2 = 0; i2 < p_block_2_.length; ++i2) {
            MatchBlock matchblock = p_block_2_[i2];
            if (!matchblock.matches(p_block_0_, p_block_1_)) continue;
            return true;
        }
        return false;
    }

    public static boolean blockId(int p_blockId_0_, MatchBlock[] p_blockId_1_) {
        if (p_blockId_1_ == null) {
            return true;
        }
        for (int i2 = 0; i2 < p_blockId_1_.length; ++i2) {
            MatchBlock matchblock = p_blockId_1_[i2];
            if (matchblock.getBlockId() != p_blockId_0_) continue;
            return true;
        }
        return false;
    }

    public static boolean metadata(int p_metadata_0_, int[] p_metadata_1_) {
        if (p_metadata_1_ == null) {
            return true;
        }
        for (int i2 = 0; i2 < p_metadata_1_.length; ++i2) {
            if (p_metadata_1_[i2] != p_metadata_0_) continue;
            return true;
        }
        return false;
    }

    public static boolean sprite(TextureAtlasSprite p_sprite_0_, TextureAtlasSprite[] p_sprite_1_) {
        if (p_sprite_1_ == null) {
            return true;
        }
        for (int i2 = 0; i2 < p_sprite_1_.length; ++i2) {
            if (p_sprite_1_[i2] != p_sprite_0_) continue;
            return true;
        }
        return false;
    }

    public static boolean biome(BiomeGenBase p_biome_0_, BiomeGenBase[] p_biome_1_) {
        if (p_biome_1_ == null) {
            return true;
        }
        for (int i2 = 0; i2 < p_biome_1_.length; ++i2) {
            if (p_biome_1_[i2] != p_biome_0_) continue;
            return true;
        }
        return false;
    }
}

