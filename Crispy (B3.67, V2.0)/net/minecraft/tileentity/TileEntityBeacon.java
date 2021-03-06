package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class TileEntityBeacon extends TileEntityLockable implements IUpdatePlayerListBox, IInventory
{
    /** List of effects that Beacon can apply */
    public static final Potion[][] effectsList = new Potion[][] {{Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}};

    /** A list of beam segments for this beacon */
    private final List beamSegments = Lists.newArrayList();
    private long beamRenderCounter;
    private float field_146014_j;
    private boolean isComplete;

    /** Level of this beacon's pyramid. */
    private int levels = -1;

    /** Primary potion effect given by this beacon. */
    private int primaryEffect;

    /** Secondary potion effect given by this beacon. */
    private int secondaryEffect;

    /** Item given to this beacon as payment. */
    private ItemStack payment;
    private String customName;

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (this.worldObj.getTotalWorldTime() % 80L == 0L)
        {
            this.updateBeacon();
        }
    }

    public void updateBeacon()
    {
        this.updateSegmentColors();
        this.addEffectsToPlayers();
    }

    private void addEffectsToPlayers()
    {
        if (this.isComplete && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0)
        {
            double var1 = (double)(this.levels * 10 + 10);
            byte var3 = 0;

            if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect)
            {
                var3 = 1;
            }

            int var4 = this.pos.getX();
            int var5 = this.pos.getY();
            int var6 = this.pos.getZ();
            AxisAlignedBB var7 = (new AxisAlignedBB((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1))).expand(var1, var1, var1).addCoord(0.0D, (double)this.worldObj.getHeight(), 0.0D);
            List var8 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, var7);
            Iterator var9 = var8.iterator();
            EntityPlayer var10;

            while (var9.hasNext())
            {
                var10 = (EntityPlayer)var9.next();
                var10.addPotionEffect(new PotionEffect(this.primaryEffect, 180, var3, true, true));
            }

            if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0)
            {
                var9 = var8.iterator();

                while (var9.hasNext())
                {
                    var10 = (EntityPlayer)var9.next();
                    var10.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true, true));
                }
            }
        }
    }

    private void updateSegmentColors()
    {
        int var1 = this.levels;
        int var2 = this.pos.getX();
        int var3 = this.pos.getY();
        int var4 = this.pos.getZ();
        this.levels = 0;
        this.beamSegments.clear();
        this.isComplete = true;
        TileEntityBeacon.BeamSegment var5 = new TileEntityBeacon.BeamSegment(EntitySheep.func_175513_a(EnumDyeColor.WHITE));
        this.beamSegments.add(var5);
        boolean var6 = true;
        int var7;

        for (var7 = var3 + 1; var7 < this.worldObj.getActualHeight(); ++var7)
        {
            BlockPos var8 = new BlockPos(var2, var7, var4);
            IBlockState var9 = this.worldObj.getBlockState(var8);
            float[] var10;

            if (var9.getBlock() == Blocks.stained_glass)
            {
                var10 = EntitySheep.func_175513_a((EnumDyeColor)var9.getValue(BlockStainedGlass.COLOR));
            }
            else
            {
                if (var9.getBlock() != Blocks.stained_glass_pane)
                {
                    if (var9.getBlock().getLightOpacity() >= 15)
                    {
                        this.isComplete = false;
                        this.beamSegments.clear();
                        break;
                    }

                    var5.incrementHeight();
                    continue;
                }

                var10 = EntitySheep.func_175513_a((EnumDyeColor)var9.getValue(BlockStainedGlassPane.COLOR));
            }

            if (!var6)
            {
                var10 = new float[] {(var5.getColors()[0] + var10[0]) / 2.0F, (var5.getColors()[1] + var10[1]) / 2.0F, (var5.getColors()[2] + var10[2]) / 2.0F};
            }

            if (Arrays.equals(var10, var5.getColors()))
            {
                var5.incrementHeight();
            }
            else
            {
                var5 = new TileEntityBeacon.BeamSegment(var10);
                this.beamSegments.add(var5);
            }

            var6 = false;
        }

        if (this.isComplete)
        {
            for (var7 = 1; var7 <= 4; this.levels = var7++)
            {
                int var14 = var3 - var7;

                if (var14 < 0)
                {
                    break;
                }

                boolean var16 = true;

                for (int var17 = var2 - var7; var17 <= var2 + var7 && var16; ++var17)
                {
                    for (int var11 = var4 - var7; var11 <= var4 + var7; ++var11)
                    {
                        Block var12 = this.worldObj.getBlockState(new BlockPos(var17, var14, var11)).getBlock();

                        if (var12 != Blocks.emerald_block && var12 != Blocks.gold_block && var12 != Blocks.diamond_block && var12 != Blocks.iron_block)
                        {
                            var16 = false;
                            break;
                        }
                    }
                }

                if (!var16)
                {
                    break;
                }
            }

            if (this.levels == 0)
            {
                this.isComplete = false;
            }
        }

        if (!this.worldObj.isRemote && this.levels == 4 && var1 < this.levels)
        {
            Iterator var13 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, (new AxisAlignedBB((double)var2, (double)var3, (double)var4, (double)var2, (double)(var3 - 4), (double)var4)).expand(10.0D, 5.0D, 10.0D)).iterator();

            while (var13.hasNext())
            {
                EntityPlayer var15 = (EntityPlayer)var13.next();
                var15.triggerAchievement(AchievementList.fullBeacon);
            }
        }
    }

    public List getBeamSegments()
    {
        return this.beamSegments;
    }

    public float shouldBeamRender()
    {
        if (!this.isComplete)
        {
            return 0.0F;
        }
        else
        {
            int var1 = (int)(this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
            this.beamRenderCounter = this.worldObj.getTotalWorldTime();

            if (var1 > 1)
            {
                this.field_146014_j -= (float)var1 / 40.0F;

                if (this.field_146014_j < 0.0F)
                {
                    this.field_146014_j = 0.0F;
                }
            }

            this.field_146014_j += 0.025F;

            if (this.field_146014_j > 1.0F)
            {
                this.field_146014_j = 1.0F;
            }

            return this.field_146014_j;
        }
    }

    /**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new S35PacketUpdateTileEntity(this.pos, 3, var1);
    }

    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.primaryEffect = compound.getInteger("Primary");
        this.secondaryEffect = compound.getInteger("Secondary");
        this.levels = compound.getInteger("Levels");
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("Primary", this.primaryEffect);
        compound.setInteger("Secondary", this.secondaryEffect);
        compound.setInteger("Levels", this.levels);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 1;
    }

    /**
     * Returns the stack in the given slot.
     *  
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(int index)
    {
        return index == 0 ? this.payment : null;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *  
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (index == 0 && this.payment != null)
        {
            if (count >= this.payment.stackSize)
            {
                ItemStack var3 = this.payment;
                this.payment = null;
                return var3;
            }
            else
            {
                this.payment.stackSize -= count;
                return new ItemStack(this.payment.getItem(), count, this.payment.getMetadata());
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     *  
     * @param index The slot to remove a stack from.
     */
    public ItemStack getStackInSlotOnClosing(int index)
    {
        if (index == 0 && this.payment != null)
        {
            ItemStack var2 = this.payment;
            this.payment = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        if (index == 0)
        {
            this.payment = stack;
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return this.hasCustomName() ? this.customName : "container.beacon";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setName(String name)
    {
        this.customName = name;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void openInventory(EntityPlayer player) {}

    public void closeInventory(EntityPlayer player) {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
    }

    public String getGuiID()
    {
        return "minecraft:beacon";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerBeacon(playerInventory, this);
    }

    public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.levels;

            case 1:
                return this.primaryEffect;

            case 2:
                return this.secondaryEffect;

            default:
                return 0;
        }
    }

    public void setField(int id, int value)
    {
        switch (id)
        {
            case 0:
                this.levels = value;
                break;

            case 1:
                this.primaryEffect = value;
                break;

            case 2:
                this.secondaryEffect = value;
        }
    }

    public int getFieldCount()
    {
        return 3;
    }

    public void clear()
    {
        this.payment = null;
    }

    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.updateBeacon();
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    public static class BeamSegment
    {
        private final float[] colors;
        private int height;

        public BeamSegment(float[] p_i45669_1_)
        {
            this.colors = p_i45669_1_;
            this.height = 1;
        }

        protected void incrementHeight()
        {
            ++this.height;
        }

        public float[] getColors()
        {
            return this.colors;
        }

        public int getHeight()
        {
            return this.height;
        }
    }
}
