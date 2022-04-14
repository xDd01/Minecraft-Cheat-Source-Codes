/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.player.ScaffoldUtil;
import cafe.corrosion.util.timer.Stopwatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

@ModuleAttributes(name="InvManager", description="Throws out unneeded items", category=Module.Category.PLAYER)
public class InventoryManager
extends Module {
    private final Stopwatch stopwatch = new Stopwatch();
    private final List<Integer> allSwords = new ArrayList<Integer>();
    private final List<Integer> trash = new ArrayList<Integer>();
    private final List<Integer>[] allArmor = new List[4];
    private int[] bestArmorSlot;
    private int bestSwordSlot;
    private final NumberProperty delay = new NumberProperty(this, "Delay", 150.0, 25.0, 1000.0, 1.0);

    public InventoryManager() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (InventoryManager.mc.currentScreen instanceof GuiInventory) {
                int windowId = InventoryManager.mc.thePlayer.inventoryContainer.windowId;
                int bestSwordSlot = this.bestSwordSlot;
                this.collectItems();
                this.collectBestArmor();
                this.collectTrash();
                if (this.stopwatch.hasElapsed(((Number)this.delay.getValue()).longValue())) {
                    if (this.trash.size() > 0) {
                        for (Integer o2 : this.trash) {
                            int slot = o2;
                            InventoryManager.mc.playerController.windowClick(windowId, slot < 9 ? slot + 36 : slot, 1, 4, InventoryManager.mc.thePlayer);
                            this.stopwatch.reset();
                        }
                    }
                    if (bestSwordSlot != -1) {
                        InventoryManager.mc.playerController.windowClick(windowId, bestSwordSlot < 9 ? bestSwordSlot + 36 : bestSwordSlot, 0, 2, InventoryManager.mc.thePlayer);
                        this.stopwatch.reset();
                    }
                    if (ScaffoldUtil.invCheck()) {
                        for (int i2 = 9; i2 < 36; ++i2) {
                            if (!ScaffoldUtil.isStackValid(InventoryManager.mc.thePlayer.inventoryContainer.getSlot(i2).getStack())) continue;
                            InventoryManager.mc.playerController.windowClick(InventoryManager.mc.thePlayer.inventoryContainer.windowId, i2, 1, 2, InventoryManager.mc.thePlayer);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void collectItems() {
        this.bestSwordSlot = -1;
        this.allSwords.clear();
        float bestSwordDamage = -1.0f;
        for (int i2 = 0; i2 < 36; ++i2) {
            ItemStack itemStack = InventoryManager.mc.thePlayer.inventory.getStackInSlot(i2);
            if (itemStack == null || itemStack.getItem() == null || !(itemStack.getItem() instanceof ItemSword)) continue;
            float damageLevel = this.getDamageLevel(itemStack);
            this.allSwords.add(i2);
            if (!(bestSwordDamage < damageLevel)) continue;
            bestSwordDamage = damageLevel;
            this.bestSwordSlot = i2;
        }
    }

    private void collectBestArmor() {
        int armorType;
        ItemArmor armor;
        ItemStack itemStack;
        int i2;
        int[] bestArmorDamageReduction = new int[4];
        this.bestArmorSlot = new int[4];
        Arrays.fill(bestArmorDamageReduction, -1);
        Arrays.fill(this.bestArmorSlot, -1);
        for (i2 = 0; i2 < this.bestArmorSlot.length; ++i2) {
            itemStack = InventoryManager.mc.thePlayer.inventory.armorItemInSlot(i2);
            this.allArmor[i2] = new ArrayList<Integer>();
            if (itemStack == null || itemStack.getItem() == null || !(itemStack.getItem() instanceof ItemArmor)) continue;
            armor = (ItemArmor)itemStack.getItem();
            bestArmorDamageReduction[i2] = armorType = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
        }
        for (i2 = 0; i2 < 36; ++i2) {
            itemStack = InventoryManager.mc.thePlayer.inventory.getStackInSlot(i2);
            if (itemStack == null || itemStack.getItem() == null || !(itemStack.getItem() instanceof ItemArmor)) continue;
            armor = (ItemArmor)itemStack.getItem();
            armorType = 3 - armor.armorType;
            this.allArmor[armorType].add(i2);
            int slotProtectionLevel = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
            if (bestArmorDamageReduction[armorType] >= slotProtectionLevel) continue;
            bestArmorDamageReduction[armorType] = slotProtectionLevel;
            this.bestArmorSlot[armorType] = i2;
        }
    }

    private void collectTrash() {
        int i2;
        this.trash.clear();
        for (i2 = 0; i2 < 36; ++i2) {
            ItemStack itemStack = InventoryManager.mc.thePlayer.inventory.getStackInSlot(i2);
            if (itemStack == null || itemStack.getItem() == null || itemStack.getItem() instanceof ItemBook || this.isValidItem(itemStack)) continue;
            this.trash.add(i2);
        }
        for (i2 = 0; i2 < this.allArmor.length; ++i2) {
            List<Integer> armorItem = this.allArmor[i2];
            if (armorItem == null) continue;
            int armorItemSize = armorItem.size();
            for (int i1 = 0; i1 < armorItemSize; ++i1) {
                Integer slot = armorItem.get(i1);
                if (slot == this.bestArmorSlot[i2]) continue;
                this.trash.add(slot);
            }
        }
        int allSwordsSize = this.allSwords.size();
        while (i2 < allSwordsSize) {
            Integer slot = this.allSwords.get(i2);
            if (slot != this.bestSwordSlot) {
                this.trash.add(slot);
            }
            ++i2;
        }
    }

    private float getDamageLevel(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            ItemSword sword = (ItemSword)stack.getItem();
            float sharpness = (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f;
            float fireAspect = (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5f;
            return sword.getDamageVsEntity() + sharpness + fireAspect;
        }
        return 0.0f;
    }

    private boolean isValidItem(ItemStack itemStack) {
        if (itemStack.getDisplayName().startsWith("\u00a7a")) {
            return true;
        }
        return itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemEnderPearl || itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemPotion && !this.isBadPotion(itemStack) || itemStack.getItem() instanceof ItemBlock && !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockChest) && !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockEnderChest) || itemStack.getDisplayName().contains("Play") || itemStack.getDisplayName().contains("Game") || itemStack.getDisplayName().contains("Right Click") || itemStack.getDisplayName().contains("Hype") || itemStack.getDisplayName().contains("Gadget");
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion)stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect o2 : potion.getEffects(stack)) {
                    if (o2.getPotionID() != Potion.poison.getId() && o2.getPotionID() != Potion.harm.getId() && o2.getPotionID() != Potion.moveSlowdown.getId() && o2.getPotionID() != Potion.weakness.getId()) continue;
                    return true;
                }
            }
        }
        return false;
    }
}

