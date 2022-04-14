package me.spec.eris.utils.player;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class InventoryUtils {
    public static boolean busy;
    public static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isHoldingSword() {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null
                && Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
            return true;
        }
        return false;
    }

    public static boolean isContainerFull(Container container) {
        boolean full = true;
        for (Slot slot : container.inventorySlots) {
            if (!slot.getHasStack()) {
                full = false;
                break;
            }
        }
        return full;
    }

    public static boolean isContainerEmpty(Container container) {
        boolean empty = true;
        for (Slot slot : container.inventorySlots) {
            if (slot.getHasStack()) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    public static float getSharpnessLevel(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword)) {
            return 0.0f;
        }
        return (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f;
    }

    public static float getItemDamage(final ItemStack itemStack) {
        float damage = ((ItemSword) itemStack.getItem()).getDamageVsEntity();
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25f;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.01f;
        return damage;
    }

    public static int getArmorItemsEquipSlot(ItemStack stack, boolean equipmentSlot) {
        if (stack.getUnlocalizedName().contains("helmet"))
            return equipmentSlot ? 4 : 5;
        if (stack.getUnlocalizedName().contains("chestplate"))
            return equipmentSlot ? 3 : 6;
        if (stack.getUnlocalizedName().contains("leggings"))
            return equipmentSlot ? 2 : 7;
        if (stack.getUnlocalizedName().contains("boots"))
            return equipmentSlot ? 1 : 8;
        return -1;
    }

    public static double getArmorProtection(ItemStack armorStack) {
        if (!(armorStack.getItem() instanceof ItemArmor))
            return 0.0;

        final ItemArmor armorItem = (ItemArmor) armorStack.getItem();
        final double protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, armorStack);

        return armorItem.damageReduceAmount + ((6 + protectionLevel * protectionLevel) * 0.75 / 3);

    }


    public static int getSwordSlot() {
        if (mc.thePlayer == null) {
            return -1;
        }

        int bestSword = -1;
        float bestDamage = 1F;

        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item != null) {
                    if (item.getItem() instanceof ItemSword) {
                        ItemSword is = (ItemSword) item.getItem();
                        float damage = is.getDamageVsEntity();
                        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, item) * 1.26F +
                                EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, item) * 0.01f;
                        if (damage > bestDamage) {
                            bestDamage = damage;
                            bestSword = i;
                        }
                    }
                }
            }
        }
        return bestSword;
    }

    public static int getEmptyHotbarSlot() {
        for (int k = 0; k < 9; ++k) {
            if (mc.thePlayer.inventory.mainInventory[k] == null) {
                return k;
            }
        }
        return -1;
    }

    public static int getPickaxeSlot() {
        if (mc.thePlayer == null) {
            return -1;
        }

        int bestSword = -1;
        float bestDamage = 1F;

        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item != null) {
                    if (item.getItem() instanceof ItemPickaxe) {
                        ItemPickaxe is = (ItemPickaxe) item.getItem();
                        float damage = is.getStrVsBlock(item, Block.getBlockById(4));
                        if (damage > bestDamage) {
                            bestDamage = damage;
                            bestSword = i;
                        }
                    }
                }
            }
        }
        return bestSword;
    }

    public static int getAxeSlot() {
        if (mc.thePlayer == null) {
            return -1;
        }

        int bestSword = -1;
        float bestDamage = 1F;

        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item != null) {
                    if (item.getItem() instanceof ItemAxe) {
                        ItemAxe is = (ItemAxe) item.getItem();
                        float damage = is.getStrVsBlock(item, Block.getBlockById(17));
                        if (damage > bestDamage) {
                            bestDamage = damage;
                            bestSword = i;
                        }
                    }
                }
            }
        }
        return bestSword;
    }

    public static int getShovelSlot() {
        if (mc.thePlayer == null) {
            return -1;
        }

        int bestSword = -1;
        float bestDamage = 1F;

        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item != null) {
                    if (item.getItem() instanceof ItemTool) {
                        ItemTool is = (ItemTool) item.getItem();
                        if (isShovel(is)) {
                            float damage = is.getStrVsBlock(item, Block.getBlockById(3));
                            if (damage > bestDamage) {
                                bestDamage = damage;
                                bestSword = i;
                            }
                        }
                    }
                }
            }
        }
        return bestSword;
    }

    public static boolean isShovel(Item is) {
        return Item.getItemById(256) == is || Item.getItemById(269) == is || Item.getItemById(273) == is || Item.getItemById(277) == is || Item.getItemById(284) == is;
    }

    public static void shiftClick(int k) {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, k, 0, 1, Minecraft.getMinecraft().thePlayer);
    }

    public static ItemStack compareProtection(ItemStack item1, ItemStack item2) {
        if (!(item1.getItem() instanceof ItemArmor) && !(item2.getItem() instanceof ItemArmor))
            return null;

        if (!(item1.getItem() instanceof ItemArmor))
            return item2;

        if (!(item2.getItem() instanceof ItemArmor))
            return item1;

        if (getArmorProtection(item1) > getArmorProtection(item2)) {
            return item1;
        } else if (getArmorProtection(item2) > getArmorProtection(item1)) {
            return item2;
        }

        return null;
    }
}
