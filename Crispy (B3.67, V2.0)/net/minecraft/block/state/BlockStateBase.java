package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

public abstract class BlockStateBase implements IBlockState
{
    private static final Joiner COMMA_JOINER = Joiner.on(',');
    private static final Function MAP_ENTRY_TO_STRING = new Function()
    {
        public String apply0(Entry entry)
        {
            if (entry == null)
            {
                return "<NULL>";
            }
            else
            {
                IProperty var2 = (IProperty)entry.getKey();
                return var2.getName() + "=" + var2.getName((Comparable)entry.getValue());
            }
        }
        public Object apply(Object p_apply_1_)
        {
            return this.apply0((Entry)p_apply_1_);
        }
    };
    private int blockId = -1;
    private int blockStateId = -1;
    private int metadata = -1;
    private ResourceLocation blockLocation = null;

    public int getBlockId()
    {
        if (this.blockId < 0)
        {
            this.blockId = Block.getIdFromBlock(this.getBlock());
        }

        return this.blockId;
    }

    public int getBlockStateId()
    {
        if (this.blockStateId < 0)
        {
            this.blockStateId = Block.getStateId(this);
        }

        return this.blockStateId;
    }

    public int getMetadata()
    {
        if (this.metadata < 0)
        {
            this.metadata = this.getBlock().getMetaFromState(this);
        }

        return this.metadata;
    }

    public ResourceLocation getBlockLocation()
    {
        if (this.blockLocation == null)
        {
            this.blockLocation = (ResourceLocation)Block.blockRegistry.getNameForObject(this.getBlock());
        }

        return this.blockLocation;
    }

    /**
     * Create a version of this BlockState with the given property cycled to the next value in order. If the property
     * was at the highest possible value, it is set to the lowest one instead.
     */
    public IBlockState cycleProperty(IProperty property)
    {
        return this.withProperty(property, (Comparable)cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
    }

    /**
     * Helper method for cycleProperty.
     *  
     * @param values The collection of values
     * @param currentValue The current value
     */
    protected static Object cyclePropertyValue(Collection values, Object currentValue)
    {
        Iterator var2 = values.iterator();

        while (var2.hasNext())
        {
            if (var2.next().equals(currentValue))
            {
                if (var2.hasNext())
                {
                    return var2.next();
                }

                return values.iterator().next();
            }
        }

        return var2.next();
    }

    public String toString()
    {
        StringBuilder var1 = new StringBuilder();
        var1.append(Block.blockRegistry.getNameForObject(this.getBlock()));

        if (!this.getProperties().isEmpty())
        {
            var1.append("[");
            COMMA_JOINER.appendTo(var1, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
            var1.append("]");
        }

        return var1.toString();
    }

    public ImmutableTable<IProperty, Comparable, IBlockState> getPropertyValueTable()
    {
        return null;
    }
}
