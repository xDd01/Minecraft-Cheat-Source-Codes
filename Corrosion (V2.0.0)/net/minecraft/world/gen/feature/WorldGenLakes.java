/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenLakes
extends WorldGenerator {
    private Block block;

    public WorldGenLakes(Block blockIn) {
        this.block = blockIn;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        position = position.add(-8, 0, -8);
        while (position.getY() > 5 && worldIn.isAirBlock(position)) {
            position = position.down();
        }
        if (position.getY() <= 4) {
            return false;
        }
        position = position.down(4);
        boolean[] aboolean = new boolean[2048];
        int i2 = rand.nextInt(4) + 4;
        for (int j2 = 0; j2 < i2; ++j2) {
            double d0 = rand.nextDouble() * 6.0 + 3.0;
            double d1 = rand.nextDouble() * 4.0 + 2.0;
            double d2 = rand.nextDouble() * 6.0 + 3.0;
            double d3 = rand.nextDouble() * (16.0 - d0 - 2.0) + 1.0 + d0 / 2.0;
            double d4 = rand.nextDouble() * (8.0 - d1 - 4.0) + 2.0 + d1 / 2.0;
            double d5 = rand.nextDouble() * (16.0 - d2 - 2.0) + 1.0 + d2 / 2.0;
            for (int l2 = 1; l2 < 15; ++l2) {
                for (int i1 = 1; i1 < 15; ++i1) {
                    for (int j1 = 1; j1 < 7; ++j1) {
                        double d6 = ((double)l2 - d3) / (d0 / 2.0);
                        double d7 = ((double)j1 - d4) / (d1 / 2.0);
                        double d8 = ((double)i1 - d5) / (d2 / 2.0);
                        double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                        if (!(d9 < 1.0)) continue;
                        aboolean[(l2 * 16 + i1) * 8 + j1] = true;
                    }
                }
            }
        }
        for (int k1 = 0; k1 < 16; ++k1) {
            for (int l2 = 0; l2 < 16; ++l2) {
                for (int k2 = 0; k2 < 8; ++k2) {
                    boolean flag;
                    boolean bl2 = flag = !aboolean[(k1 * 16 + l2) * 8 + k2] && (k1 < 15 && aboolean[((k1 + 1) * 16 + l2) * 8 + k2] || k1 > 0 && aboolean[((k1 - 1) * 16 + l2) * 8 + k2] || l2 < 15 && aboolean[(k1 * 16 + l2 + 1) * 8 + k2] || l2 > 0 && aboolean[(k1 * 16 + (l2 - 1)) * 8 + k2] || k2 < 7 && aboolean[(k1 * 16 + l2) * 8 + k2 + 1] || k2 > 0 && aboolean[(k1 * 16 + l2) * 8 + (k2 - 1)]);
                    if (!flag) continue;
                    Material material = worldIn.getBlockState(position.add(k1, k2, l2)).getBlock().getMaterial();
                    if (k2 >= 4 && material.isLiquid()) {
                        return false;
                    }
                    if (k2 >= 4 || material.isSolid() || worldIn.getBlockState(position.add(k1, k2, l2)).getBlock() == this.block) continue;
                    return false;
                }
            }
        }
        for (int l1 = 0; l1 < 16; ++l1) {
            for (int i3 = 0; i3 < 16; ++i3) {
                for (int i4 = 0; i4 < 8; ++i4) {
                    if (!aboolean[(l1 * 16 + i3) * 8 + i4]) continue;
                    worldIn.setBlockState(position.add(l1, i4, i3), i4 >= 4 ? Blocks.air.getDefaultState() : this.block.getDefaultState(), 2);
                }
            }
        }
        for (int i22 = 0; i22 < 16; ++i22) {
            for (int j3 = 0; j3 < 16; ++j3) {
                for (int j4 = 4; j4 < 8; ++j4) {
                    BlockPos blockpos;
                    if (!aboolean[(i22 * 16 + j3) * 8 + j4] || worldIn.getBlockState(blockpos = position.add(i22, j4 - 1, j3)).getBlock() != Blocks.dirt || worldIn.getLightFor(EnumSkyBlock.SKY, position.add(i22, j4, j3)) <= 0) continue;
                    BiomeGenBase biomegenbase = worldIn.getBiomeGenForCoords(blockpos);
                    if (biomegenbase.topBlock.getBlock() == Blocks.mycelium) {
                        worldIn.setBlockState(blockpos, Blocks.mycelium.getDefaultState(), 2);
                        continue;
                    }
                    worldIn.setBlockState(blockpos, Blocks.grass.getDefaultState(), 2);
                }
            }
        }
        if (this.block.getMaterial() == Material.lava) {
            for (int j2 = 0; j2 < 16; ++j2) {
                for (int k3 = 0; k3 < 16; ++k3) {
                    for (int k4 = 0; k4 < 8; ++k4) {
                        boolean flag1;
                        boolean bl3 = flag1 = !aboolean[(j2 * 16 + k3) * 8 + k4] && (j2 < 15 && aboolean[((j2 + 1) * 16 + k3) * 8 + k4] || j2 > 0 && aboolean[((j2 - 1) * 16 + k3) * 8 + k4] || k3 < 15 && aboolean[(j2 * 16 + k3 + 1) * 8 + k4] || k3 > 0 && aboolean[(j2 * 16 + (k3 - 1)) * 8 + k4] || k4 < 7 && aboolean[(j2 * 16 + k3) * 8 + k4 + 1] || k4 > 0 && aboolean[(j2 * 16 + k3) * 8 + (k4 - 1)]);
                        if (!flag1 || k4 >= 4 && rand.nextInt(2) == 0 || !worldIn.getBlockState(position.add(j2, k4, k3)).getBlock().getMaterial().isSolid()) continue;
                        worldIn.setBlockState(position.add(j2, k4, k3), Blocks.stone.getDefaultState(), 2);
                    }
                }
            }
        }
        if (this.block.getMaterial() == Material.water) {
            for (int k2 = 0; k2 < 16; ++k2) {
                for (int l3 = 0; l3 < 16; ++l3) {
                    int l4 = 4;
                    if (!worldIn.canBlockFreezeWater(position.add(k2, l4, l3))) continue;
                    worldIn.setBlockState(position.add(k2, l4, l3), Blocks.ice.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}
