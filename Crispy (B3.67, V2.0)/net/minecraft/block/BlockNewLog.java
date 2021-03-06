package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockNewLog extends BlockLog
{
    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, new Predicate()
    {
        public boolean apply(BlockPlanks.EnumType type)
        {
            return type.getMetadata() >= 4;
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.apply((BlockPlanks.EnumType)p_apply_1_);
        }
    });

    public BlockNewLog()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState var2 = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) + 4));

        switch (meta & 12)
        {
            case 0:
                var2 = var2.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;

            case 4:
                var2 = var2.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;

            case 8:
                var2 = var2.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;

            default:
                var2 = var2.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }

        return var2;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        byte var2 = 0;
        int var3 = var2 | ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata() - 4;

        switch (BlockNewLog.SwitchEnumAxis.AXIS_LOOKUP[((BlockLog.EnumAxis)state.getValue(LOG_AXIS)).ordinal()])
        {
            case 1:
                var3 |= 4;
                break;

            case 2:
                var3 |= 8;
                break;

            case 3:
                var3 |= 12;
        }

        return var3;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT, LOG_AXIS});
    }

    protected ItemStack createStackedBlock(IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata() - 4);
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata() - 4;
    }

    static final class SwitchEnumAxis
    {
        static final int[] AXIS_LOOKUP = new int[BlockLog.EnumAxis.values().length];

        static
        {
            try
            {
                AXIS_LOOKUP[BlockLog.EnumAxis.X.ordinal()] = 1;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                AXIS_LOOKUP[BlockLog.EnumAxis.Z.ordinal()] = 2;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                AXIS_LOOKUP[BlockLog.EnumAxis.NONE.ordinal()] = 3;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
