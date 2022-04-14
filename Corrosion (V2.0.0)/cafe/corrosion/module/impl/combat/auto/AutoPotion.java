/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat.auto;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.packet.PacketUtil;
import cafe.corrosion.util.timer.Stopwatch;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;

@ModuleAttributes(name="Auto Potion", description="Automatically splashes you with health potions", category=Module.Category.COMBAT)
public class AutoPotion
extends Module {
    private static final List<Integer> VALID_POTIONS = Arrays.asList(Potion.moveSpeed.id, Potion.regeneration.id, Potion.heal.id, Potion.jump.id);
    private final Stopwatch stopwatch = new Stopwatch();
    private final NumberProperty delay = new NumberProperty(this, "Delay", 150, 50, 1000, 50);
    private final NumberProperty health = new NumberProperty(this, "Health", 15, 1, 18, 0.5);
    private static boolean potting;
    private boolean swapNextTick;

    public AutoPotion() {
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            if (AutoPotion.mc.thePlayer == null || AutoPotion.mc.thePlayer.inventoryContainer == null || !AutoPotion.mc.thePlayer.onGround) {
                return;
            }
            if (!eventUpdate.isPre()) {
                return;
            }
            if (this.swapNextTick) {
                PacketUtil.sendNoEvent(new C09PacketHeldItemChange(AutoPotion.mc.thePlayer.inventory.currentItem));
                this.swapNextTick = false;
                return;
            }
            if (this.stopwatch.hasElapsed(((Number)this.delay.getValue()).longValue())) {
                if (potting) {
                    PacketUtil.sendNoEvent(new C09PacketHeldItemChange(3));
                    PacketUtil.sendNoEvent(new C08PacketPlayerBlockPlacement(null));
                    potting = false;
                    this.swapNextTick = true;
                    this.stopwatch.reset();
                } else if (AutoPotion.mc.thePlayer.getHealth() <= ((Number)this.health.getValue()).floatValue()) {
                    eventUpdate.setRotationPitch(90.0f);
                    potting = true;
                    this.movePotions();
                }
            } else {
                potting = false;
            }
        });
    }

    private void movePotions() {
        for (int i2 = 0; i2 < 45; ++i2) {
            ItemStack stack = AutoPotion.mc.thePlayer.inventoryContainer.getSlot(i2).getStack();
            if (stack == null || !this.isValid(stack)) continue;
            this.swap(i2, 3);
            return;
        }
    }

    private boolean isValid(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (!(item instanceof ItemPotion)) {
            return false;
        }
        ItemPotion itemPotion = (ItemPotion)item;
        if (!ItemPotion.isSplash(itemStack.getMetadata())) {
            return false;
        }
        return itemPotion.getEffects(itemStack).stream().filter(potionEffect -> VALID_POTIONS.contains(potionEffect.getPotionID()) && !AutoPotion.mc.thePlayer.isPotionActive(potionEffect.getPotionID())).anyMatch(potionEffect -> potionEffect.getPotionID() == Potion.heal.getId() && AutoPotion.mc.thePlayer.getHealth() <= ((Number)this.health.getValue()).floatValue());
    }

    public void swap(int slot, int hotBarNumber) {
        AutoPotion.mc.playerController.windowClick(AutoPotion.mc.thePlayer.inventoryContainer.windowId, slot, hotBarNumber, 2, AutoPotion.mc.thePlayer);
    }

    public static boolean isPotting() {
        return potting;
    }
}

