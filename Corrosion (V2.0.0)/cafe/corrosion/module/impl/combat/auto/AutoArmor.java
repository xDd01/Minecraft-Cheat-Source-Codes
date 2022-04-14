/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat.auto;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.timer.Stopwatch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@ModuleAttributes(name="Auto Armor", description="Automatically applies the best armor in your inventory", category=Module.Category.COMBAT)
public class AutoArmor
extends Module {
    private final NumberProperty delay = new NumberProperty(this, "Delay", 150.0, 50, 1000.0, 50);
    private final Stopwatch stopwatch = new Stopwatch();

    public AutoArmor() {
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            if (!eventUpdate.isPre()) {
                return;
            }
            AutoArmor.mc.playerController.updateController();
            this.equipArmor(((Number)this.delay.getValue()).intValue());
        });
    }

    public void equipArmor(int delay) {
        for (int i2 = 9; i2 < 45; ++i2) {
            ItemStack stackInSlot;
            if (!AutoArmor.mc.thePlayer.inventoryContainer.getSlot(i2).getHasStack() || !((stackInSlot = AutoArmor.mc.thePlayer.inventoryContainer.getSlot(i2).getStack()).getItem() instanceof ItemArmor) || this.getArmorItemsEquipSlot(stackInSlot, false) == -1) continue;
            if (AutoArmor.mc.thePlayer.getEquipmentInSlot(this.getArmorItemsEquipSlot(stackInSlot, true)) == null) {
                if (!this.stopwatch.hasElapsed(delay)) continue;
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.thePlayer.inventoryContainer.windowId, i2, 0, 0, AutoArmor.mc.thePlayer);
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.thePlayer.inventoryContainer.windowId, this.getArmorItemsEquipSlot(stackInSlot, false), 0, 0, AutoArmor.mc.thePlayer);
                this.stopwatch.reset();
                return;
            }
            ItemStack stackInEquipmentSlot = AutoArmor.mc.thePlayer.getEquipmentInSlot(this.getArmorItemsEquipSlot(stackInSlot, true));
            if (this.compareProtection(stackInSlot, stackInEquipmentSlot) != stackInSlot) continue;
            System.out.println("Stack in slot : " + stackInSlot.getUnlocalizedName());
            if (!this.stopwatch.hasElapsed(delay)) continue;
            AutoArmor.mc.playerController.windowClick(AutoArmor.mc.thePlayer.inventoryContainer.windowId, i2, 0, 0, AutoArmor.mc.thePlayer);
            AutoArmor.mc.playerController.windowClick(AutoArmor.mc.thePlayer.inventoryContainer.windowId, this.getArmorItemsEquipSlot(stackInSlot, false), 0, 0, AutoArmor.mc.thePlayer);
            AutoArmor.mc.playerController.windowClick(AutoArmor.mc.thePlayer.inventoryContainer.windowId, i2, 0, 0, AutoArmor.mc.thePlayer);
            return;
        }
    }

    public int getArmorItemsEquipSlot(ItemStack stack, boolean equipmentSlot) {
        if (stack.getUnlocalizedName().contains("helmet")) {
            return equipmentSlot ? 4 : 5;
        }
        if (stack.getUnlocalizedName().contains("chestplate")) {
            return equipmentSlot ? 3 : 6;
        }
        if (stack.getUnlocalizedName().contains("leggings")) {
            return equipmentSlot ? 2 : 7;
        }
        if (stack.getUnlocalizedName().contains("boots")) {
            return equipmentSlot ? 1 : 8;
        }
        return -1;
    }

    public ItemStack compareProtection(ItemStack item1, ItemStack item2) {
        if (!(item1.getItem() instanceof ItemArmor) && !(item2.getItem() instanceof ItemArmor)) {
            return null;
        }
        if (!(item1.getItem() instanceof ItemArmor)) {
            return item2;
        }
        if (!(item2.getItem() instanceof ItemArmor)) {
            return item1;
        }
        if (this.getArmorProtection(item1) > this.getArmorProtection(item2)) {
            return item1;
        }
        if (this.getArmorProtection(item2) > this.getArmorProtection(item1)) {
            return item2;
        }
        return null;
    }

    public double getArmorProtection(ItemStack armorStack) {
        if (!(armorStack.getItem() instanceof ItemArmor)) {
            return 0.0;
        }
        ItemArmor armorItem = (ItemArmor)armorStack.getItem();
        double protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, armorStack);
        return (double)armorItem.damageReduceAmount + (6.0 + protectionLevel * protectionLevel) * 0.75 / 3.0;
    }
}

