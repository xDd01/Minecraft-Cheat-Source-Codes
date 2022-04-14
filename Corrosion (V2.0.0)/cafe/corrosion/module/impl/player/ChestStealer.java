/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.player.ContainerUtil;
import cafe.corrosion.util.timer.Stopwatch;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;

@ModuleAttributes(name="ChestStealer", description="Automatically takes items out of chests", category=Module.Category.PLAYER)
public class ChestStealer
extends Module {
    private final Stopwatch stopwatch = new Stopwatch();
    private final NumberProperty delay = new NumberProperty(this, "Delay", 150.0, 25.0, 1000.0, 1);
    private final BooleanProperty checkName = new BooleanProperty((Module)this, "Check Name", true);

    public ChestStealer() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (ChestStealer.mc.currentScreen instanceof GuiChest) {
                if (!ContainerUtil.isEmpty(ChestStealer.mc.thePlayer.openContainer)) {
                    if (((Boolean)this.checkName.getValue()).booleanValue() && !ContainerUtil.isNameValid(ChestStealer.mc.thePlayer.openContainer)) {
                        return;
                    }
                    for (Slot slot : ChestStealer.mc.thePlayer.openContainer.inventorySlots) {
                        if (slot == null || slot.getStack() == null || !this.stopwatch.hasElapsed(((Number)this.delay.getValue()).longValue())) continue;
                        ChestStealer.mc.playerController.windowClick(ChestStealer.mc.thePlayer.openContainer.windowId, slot.slotNumber, 0, 1, ChestStealer.mc.thePlayer);
                        ChestStealer.mc.playerController.updateController();
                        this.stopwatch.reset();
                    }
                } else {
                    ChestStealer.mc.thePlayer.closeScreen();
                }
            }
        });
    }

    @Override
    public String getMode() {
        return ((Number)this.delay.getValue()).intValue() + "";
    }
}

