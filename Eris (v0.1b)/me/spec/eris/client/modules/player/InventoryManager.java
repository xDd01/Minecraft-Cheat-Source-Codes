package me.spec.eris.client.modules.player;

import java.util.concurrent.ThreadLocalRandom;

import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.utils.player.InventoryUtils;
import me.spec.eris.utils.player.PlayerUtils;
import me.spec.eris.utils.world.TimerUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

public class InventoryManager extends Module {

    public InventoryManager(String racism) {
        super("InventoryManager", ModuleCategory.PLAYER, racism);
    }

    public BooleanValue<Boolean> clean = new BooleanValue<>("Clean", true, this);
    public BooleanValue<Boolean> keepAxe = new BooleanValue<>("Axe", true, this);
    public BooleanValue<Boolean> keepShovel = new BooleanValue<>("Shovel", true, this);
    public BooleanValue<Boolean> keepPickAxe = new BooleanValue<>("Pickaxe", true, this);
    public BooleanValue<Boolean> swordSlot = new BooleanValue<>("Sword lot", true, this);
    public BooleanValue<Boolean> cleanBad = new BooleanValue<>("Best Items", true, this);
    public BooleanValue<Boolean> autoArmor = new BooleanValue<>("Auto Armor", true, this);
    public NumberValue<Integer> swordsSlot = new NumberValue<>("Sword Slot", 1, 1, 9, this);
    public NumberValue<Integer> delay = new NumberValue<>("Delay", 150, 1, 1000, this);
    private final TimerUtils timer = new TimerUtils();
    private int lastSlot = -1;
    private boolean withinInventory;


    @Override
    public void onEvent(Event e) {
        if (mc.currentScreen instanceof GuiChest) return;
        if (e instanceof EventPacket) {
            EventPacket event = (EventPacket) e;
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiChest) || mc.thePlayer.isUsingItem()) {
                timer.reset();
                return;
            }
            if (event.isSending()) {
                if (event.getPacket() instanceof C0DPacketCloseWindow) {
                    withinInventory = false;
                }
                if (event.getPacket() instanceof C16PacketClientStatus) {
                    C16PacketClientStatus status = (C16PacketClientStatus) event.getPacket();
                    if (status.getStatus() == EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                        withinInventory = true;
                    }
                }
                if (event.getPacket() instanceof C09PacketHeldItemChange) {
                    C09PacketHeldItemChange packet = (C09PacketHeldItemChange) event.getPacket();
                    lastSlot = packet.getSlotId();
                }
            }
        }
        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiChest) || mc.thePlayer.isUsingItem()) {
                timer.reset();
                return;
            }
            if (event.isPre()) return;
            if (this.lastSlot != -1 && this.lastSlot != mc.thePlayer.inventory.currentItem) {
                mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            double realdelay = delay.getValue();
            int delay = (int) Math.max(95, realdelay + ThreadLocalRandom.current().nextDouble(-30, 30));
            if (timer.hasReached(delay)) {
                if (!withinInventory) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                }
                invManager(delay);
                if (autoArmor.getValue()) {
                    autoArmor(delay);
                }
                if (withinInventory) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0DPacketCloseWindow(0));
                    withinInventory = false;
                }
                timer.reset();
            }
        }
    }


    private void invManager(int delay) {
        int bestSword = -1;
        float bestDamage = 1F;

        for (int k = 0; k < mc.thePlayer.inventory.mainInventory.length; k++) {
            ItemStack item = mc.thePlayer.inventory.mainInventory[k];
            if (item != null) {
                if (item.getItem() instanceof ItemSword) {
                    ItemSword is = (ItemSword) item.getItem();
                    float damage = is.getDamageVsEntity();
                    damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, item) * 1.26F +
                            EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, item) * 0.01f;
                    if (damage > bestDamage) {
                        bestDamage = damage;
                        bestSword = k;
                    }
                }
            }
        }
        int swordSlot = 1;
        if (bestSword != -1 && bestSword != swordSlot - 1) {
            for (int i = 0; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); i++) {
                Slot s = mc.thePlayer.inventoryContainer.inventorySlots.get(i);
                if (s.getHasStack() && s.getStack() == mc.thePlayer.inventory.mainInventory[bestSword]) {
                    int slot = swordSlot - 1;
                    //ClientLogger.printToMinecraft("Put sword in slot: 1");
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, s.slotNumber, slot, 2, mc.thePlayer);
                    timer.reset();
                    return;
                }
            }
        }
        if (this.clean.getValue() && timer.hasReached(delay)) {
            for (int i = 9; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (this.shouldDrop(is, i) && timer.hasReached(delay)) {

                        //		ClientLogger.printToMinecraft("Dropped item: " + is.getDisplayName());
                        this.drop(i);
                        this.timer.reset();
                        break;
                    }
                }
            }
        }
    }

    private void autoArmor(double delay) {
        int bestHelm = this.getBestHelmet();
        if (mc.thePlayer.inventory.armorItemInSlot(3) == null) {
            if (bestHelm != -1) {
                if (bestHelm < 9 && mc.thePlayer.inventory.getStackInSlot(bestHelm).getItem() instanceof ItemArmor) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(bestHelm));
                    mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bestHelm)));
                } else {
                    this.shiftClick(bestHelm);
                }
                timer.reset();
                return;
            }
        } else if (bestHelm != -1 && mc.thePlayer.inventory.armorItemInSlot(3) != mc.thePlayer.inventoryContainer.getSlot(bestHelm).getStack()) {
            this.drop(5);
            timer.reset();
            return;
        }
        int bestChest = this.getBestChestplate();
        if (mc.thePlayer.inventory.armorItemInSlot(2) == null) {
            if (bestChest != -1) {
                if (bestChest < 9 && mc.thePlayer.inventory.getStackInSlot(bestChest).getItem() instanceof ItemArmor) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(bestChest));
                    mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bestChest)));
                } else {
                    this.shiftClick(bestChest);
                }
                timer.reset();
                return;
            }
        } else if (bestChest != -1 && mc.thePlayer.inventory.armorItemInSlot(2) != mc.thePlayer.inventoryContainer.getSlot(bestChest).getStack()) {
            this.drop(6);
            timer.reset();
            return;
        }
        int bestLegs = this.getBestLeggings();
        if (mc.thePlayer.inventory.armorItemInSlot(1) == null) {
            if (bestLegs != -1) {
                if (bestLegs < 9 && mc.thePlayer.inventory.getStackInSlot(bestLegs).getItem() instanceof ItemArmor) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(bestLegs));
                    mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bestLegs)));
                } else {
                    this.shiftClick(bestLegs);
                    timer.reset();
                    return;
                }
            }
        } else if (bestLegs != -1 && mc.thePlayer.inventory.armorItemInSlot(1) != mc.thePlayer.inventoryContainer.getSlot(bestLegs).getStack()) {
            this.drop(7);
            timer.reset();
            return;
        }
        int bestBoot = this.getBestBoots();
        if (mc.thePlayer.inventory.armorItemInSlot(0) == null) {
            if (bestBoot != -1) {
                if (bestBoot < 9 && mc.thePlayer.inventory.getStackInSlot(bestBoot).getItem() instanceof ItemArmor) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(bestBoot));
                    mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bestBoot)));
                } else {
                    this.shiftClick(bestBoot);
                }
                timer.reset();
                return;
            }
        } else if (bestBoot != -1 && mc.thePlayer.inventory.armorItemInSlot(0) != mc.thePlayer.inventoryContainer.getSlot(bestBoot).getStack()) {
            this.drop(8);
            timer.reset();
            return;
        }

        boolean dropped = false;

        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is != null && is.getItem() instanceof ItemArmor && !dropped) {
                    dropped = true;
                    timer.reset();
                    this.drop(i);
                    return;
                }
            }
        }
    }

    public void drop(final int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }

    public boolean shouldDrop(ItemStack is, int k) {
        int bestSword = InventoryUtils.getSwordSlot();
        if (is.hasDisplayName()) return false;
        if (is.getItem() instanceof ItemSword) {
            if (bestSword != -1 && bestSword != k) {
                return true;
            }
        }
        int bestPick = InventoryUtils.getPickaxeSlot();
        if (is.getItem() instanceof ItemPickaxe) {
            if (!keepPickAxe.getValue()) {
                return true;
            }
            if (bestPick != -1 && bestPick != k) {
                return true;
            }
        }

        int bestAxe = InventoryUtils.getAxeSlot();
        if (is.getItem() instanceof ItemAxe) {
            if (!keepAxe.getValue()) {
                return true;
            }
            if (bestAxe != -1 && bestAxe != k) {
                return true;
            }
        }

        int bestShovel = InventoryUtils.getShovelSlot();
        if (InventoryUtils.isShovel(is.getItem())) {
            if (!keepShovel.getValue()) {
                return true;
            }
            if (bestShovel != -1 && bestShovel != k) {
                return true;
            }
        }
        if (cleanBad.getValue() && PlayerUtils.isBad(is)) {
            return true;
        }
        return false;
    }

    private int getBestHelmet() {
        int bestSword = -1;
        float bestValue = 0F;

        for (int k = 0; k < 36; k++) {
            if (mc.thePlayer.inventoryContainer.getSlot(k).getHasStack()) {
                ItemStack item = mc.thePlayer.inventoryContainer.getSlot(k).getStack();
                if (item != null) {
                    if (item.getItem() instanceof ItemArmor) {
                        ItemArmor ia = (ItemArmor) item.getItem();
                        float value = getValue(item, ia);
                        if (ia.armorType == 0 && value > bestValue) {
                            bestValue = value;
                            bestSword = k;
                        }
                    }
                }
            }
        }

        for (int k = 0; k < 9; k++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(k);
            if (item != null) {
                if (item.getItem() instanceof ItemArmor) {
                    ItemArmor ia = (ItemArmor) item.getItem();
                    float value = getValue(item, ia);
                    if (ia.armorType == 0 && value > bestValue) {
                        bestValue = value;
                        bestSword = k;
                    }
                }
            }
        }
        return bestSword;
    }

    private int getBestChestplate() {
        int bestSword = -1;
        float bestValue = 0F;

        for (int k = 0; k < 36; k++) {
            if (mc.thePlayer.inventoryContainer.getSlot(k).getHasStack()) {
                ItemStack item = mc.thePlayer.inventoryContainer.getSlot(k).getStack();
                if (item != null) {
                    if (item.getItem() instanceof ItemArmor) {
                        ItemArmor ia = (ItemArmor) item.getItem();
                        float value = getValue(item, ia);
                        if (ia.armorType == 1 && value > bestValue) {
                            bestValue = value;
                            bestSword = k;
                        }
                    }
                }
            }
        }

        for (int k = 0; k < 9; k++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(k);
            if (item != null) {
                if (item.getItem() instanceof ItemArmor) {
                    ItemArmor ia = (ItemArmor) item.getItem();
                    float value = getValue(item, ia);
                    if (ia.armorType == 1 && value > bestValue) {
                        bestValue = value;
                        bestSword = k;
                    }
                }
            }
        }
        return bestSword;
    }

    private int getBestLeggings() {
        int bestSword = -1;
        float bestValue = 0F;

        for (int k = 0; k < 36; k++) {
            if (mc.thePlayer.inventoryContainer.getSlot(k).getHasStack()) {
                ItemStack item = mc.thePlayer.inventoryContainer.getSlot(k).getStack();
                if (item != null) {
                    if (item.getItem() instanceof ItemArmor) {
                        ItemArmor ia = (ItemArmor) item.getItem();
                        float value = getValue(item, ia);
                        if (ia.armorType == 2 && value > bestValue) {
                            bestValue = value;
                            bestSword = k;
                        }
                    }
                }
            }
        }

        for (int k = 0; k < 9; k++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(k);
            if (item != null) {
                if (item.getItem() instanceof ItemArmor) {
                    ItemArmor ia = (ItemArmor) item.getItem();
                    float value = getValue(item, ia);
                    if (ia.armorType == 2 && value > bestValue) {
                        bestValue = value;
                        bestSword = k;
                    }
                }
            }
        }
        return bestSword;
    }

    private int getBestBoots() {
        int bestSword = -1;
        float bestValue = 0F;

        for (int k = 0; k < 36; k++) {
            if (mc.thePlayer.inventoryContainer.getSlot(k).getHasStack()) {
                ItemStack item = mc.thePlayer.inventoryContainer.getSlot(k).getStack();

                if (item != null) {
                    if (item.getItem() instanceof ItemArmor) {
                        ItemArmor ia = (ItemArmor) item.getItem();
                        float value = getValue(item, ia);
                        if (ia.armorType == 3 && value > bestValue) {
                            bestValue = value;
                            bestSword = k;
                        }
                    }
                }
            }
        }

        for (int k = 0; k < 9; k++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(k);
            if (item != null) {
                if (item.getItem() instanceof ItemArmor) {
                    ItemArmor ia = (ItemArmor) item.getItem();
                    float value = getValue(item, ia);
                    if (ia.armorType == 3 && value > bestValue) {
                        bestValue = value;
                        bestSword = k;
                    }
                }
            }
        }
        return bestSword;
    }

    private float getValue(ItemStack is, ItemArmor ia) {
        int type = 0;
        if (ia.armorType == 0) {
            type = 0;
        }
        if (ia.armorType == 3) {
            type = 1;
        }
        if (ia.armorType == 2) {
            type = 2;
        }
        if (ia.armorType == 1) {
            type = 3;
        }

        int render = 0;
        if (ia.renderIndex == 0) {
            render = 0;
        }
        if (ia.renderIndex == 1) {
            render = 1;
        }
        if (ia.renderIndex == 4) {
            render = 2;
        }
        if (ia.renderIndex == 2) {
            render = 3;
        }
        if (ia.renderIndex == 3) {
            render = 4;
        }

        float value = (type + 1) * (render + 1);
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, is) * 2.5f;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, is) * 1.25f;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, is) * 1f;

        return value;
    }

    public void shiftClick(int slot) {
        if (mc.thePlayer.inventoryContainer.getSlot(slot).getHasStack()) {
            Slot s = mc.thePlayer.inventoryContainer.getSlot(slot);
            if (s.getStack().getItem() instanceof ItemArmor) {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


}
