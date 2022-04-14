package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIHarvestFarmland extends EntityAIMoveToBlock {
   private final EntityVillager field_179504_c;
   private boolean field_179503_e;
   private static final String __OBFID = "CL_00002253";
   private int field_179501_f;
   private boolean field_179502_d;

   public void resetTask() {
      super.resetTask();
   }

   public boolean continueExecuting() {
      return this.field_179501_f >= 0 && super.continueExecuting();
   }

   public boolean shouldExecute() {
      if (this.field_179496_a <= 0) {
         if (!this.field_179504_c.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            return false;
         }

         this.field_179501_f = -1;
         this.field_179502_d = this.field_179504_c.func_175556_cs();
         this.field_179503_e = this.field_179504_c.func_175557_cr();
      }

      return super.shouldExecute();
   }

   public EntityAIHarvestFarmland(EntityVillager var1, double var2) {
      super(var1, var2, 16);
      this.field_179504_c = var1;
   }

   protected boolean func_179488_a(World var1, BlockPos var2) {
      Block var3 = var1.getBlockState(var2).getBlock();
      if (var3 == Blocks.farmland) {
         var2 = var2.offsetUp();
         IBlockState var4 = var1.getBlockState(var2);
         var3 = var4.getBlock();
         if (var3 instanceof BlockCrops && (Integer)var4.getValue(BlockCrops.AGE) == 7 && this.field_179503_e && (this.field_179501_f == 0 || this.field_179501_f < 0)) {
            this.field_179501_f = 0;
            return true;
         }

         if (var3 == Blocks.air && this.field_179502_d && (this.field_179501_f == 1 || this.field_179501_f < 0)) {
            this.field_179501_f = 1;
            return true;
         }
      }

      return false;
   }

   public void startExecuting() {
      super.startExecuting();
   }

   public void updateTask() {
      super.updateTask();
      this.field_179504_c.getLookHelper().setLookPosition((double)this.field_179494_b.getX() + 0.5D, (double)(this.field_179494_b.getY() + 1), (double)this.field_179494_b.getZ() + 0.5D, 10.0F, (float)this.field_179504_c.getVerticalFaceSpeed());
      if (this.func_179487_f()) {
         World var1 = this.field_179504_c.worldObj;
         BlockPos var2 = this.field_179494_b.offsetUp();
         IBlockState var3 = var1.getBlockState(var2);
         Block var4 = var3.getBlock();
         if (this.field_179501_f == 0 && var4 instanceof BlockCrops && (Integer)var3.getValue(BlockCrops.AGE) == 7) {
            var1.destroyBlock(var2, true);
         } else if (this.field_179501_f == 1 && var4 == Blocks.air) {
            InventoryBasic var5 = this.field_179504_c.func_175551_co();

            for(int var6 = 0; var6 < var5.getSizeInventory(); ++var6) {
               ItemStack var7 = var5.getStackInSlot(var6);
               boolean var8 = false;
               if (var7 != null) {
                  if (var7.getItem() == Items.wheat_seeds) {
                     var1.setBlockState(var2, Blocks.wheat.getDefaultState(), 3);
                     var8 = true;
                  } else if (var7.getItem() == Items.potato) {
                     var1.setBlockState(var2, Blocks.potatoes.getDefaultState(), 3);
                     var8 = true;
                  } else if (var7.getItem() == Items.carrot) {
                     var1.setBlockState(var2, Blocks.carrots.getDefaultState(), 3);
                     var8 = true;
                  }
               }

               if (var8) {
                  --var7.stackSize;
                  if (var7.stackSize <= 0) {
                     var5.setInventorySlotContents(var6, (ItemStack)null);
                  }
                  break;
               }
            }
         }

         this.field_179501_f = -1;
         this.field_179496_a = 10;
      }

   }
}