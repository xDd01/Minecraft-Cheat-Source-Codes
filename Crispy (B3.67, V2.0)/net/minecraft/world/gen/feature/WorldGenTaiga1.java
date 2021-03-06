package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenAbstractTree
{

    public WorldGenTaiga1()
    {
        super(false);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int var4 = rand.nextInt(5) + 7;
        int var5 = var4 - rand.nextInt(2) - 3;
        int var6 = var4 - var5;
        int var7 = 1 + rand.nextInt(var6 + 1);
        boolean var8 = true;

        if (position.getY() >= 1 && position.getY() + var4 + 1 <= 256)
        {
            int var11;
            int var12;
            int var18;

            for (int var9 = position.getY(); var9 <= position.getY() + 1 + var4 && var8; ++var9)
            {
                boolean var10 = true;

                if (var9 - position.getY() < var5)
                {
                    var18 = 0;
                }
                else
                {
                    var18 = var7;
                }

                for (var11 = position.getX() - var18; var11 <= position.getX() + var18 && var8; ++var11)
                {
                    for (var12 = position.getZ() - var18; var12 <= position.getZ() + var18 && var8; ++var12)
                    {
                        if (var9 >= 0 && var9 < 256)
                        {
                            if (!this.func_150523_a(worldIn.getBlockState(new BlockPos(var11, var9, var12)).getBlock()))
                            {
                                var8 = false;
                            }
                        }
                        else
                        {
                            var8 = false;
                        }
                    }
                }
            }

            if (!var8)
            {
                return false;
            }
            else
            {
                Block var17 = worldIn.getBlockState(position.down()).getBlock();

                if ((var17 == Blocks.grass || var17 == Blocks.dirt) && position.getY() < 256 - var4 - 1)
                {
                    this.func_175921_a(worldIn, position.down());
                    var18 = 0;

                    for (var11 = position.getY() + var4; var11 >= position.getY() + var5; --var11)
                    {
                        for (var12 = position.getX() - var18; var12 <= position.getX() + var18; ++var12)
                        {
                            int var13 = var12 - position.getX();

                            for (int var14 = position.getZ() - var18; var14 <= position.getZ() + var18; ++var14)
                            {
                                int var15 = var14 - position.getZ();

                                if (Math.abs(var13) != var18 || Math.abs(var15) != var18 || var18 <= 0)
                                {
                                    BlockPos var16 = new BlockPos(var12, var11, var14);

                                    if (!worldIn.getBlockState(var16).getBlock().isFullBlock())
                                    {
                                        this.setBlock(worldIn, var16, Blocks.leaves, BlockPlanks.EnumType.SPRUCE.getMetadata());
                                    }
                                }
                            }
                        }

                        if (var18 >= 1 && var11 == position.getY() + var5 + 1)
                        {
                            --var18;
                        }
                        else if (var18 < var7)
                        {
                            ++var18;
                        }
                    }

                    for (var11 = 0; var11 < var4 - 1; ++var11)
                    {
                        Block var19 = worldIn.getBlockState(position.up(var11)).getBlock();

                        if (var19.getMaterial() == Material.air || var19.getMaterial() == Material.leaves)
                        {
                            this.setBlock(worldIn, position.up(var11), Blocks.log, BlockPlanks.EnumType.SPRUCE.getMetadata());
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
