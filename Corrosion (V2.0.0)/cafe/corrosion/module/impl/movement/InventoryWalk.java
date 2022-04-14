/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

@ModuleAttributes(name="InventoryWalk", description="Allows you to walk while your inventory is open", category=Module.Category.MOVEMENT)
public class InventoryWalk
extends Module {
    private final KeyBinding[] keyBindings;

    public InventoryWalk() {
        this.keyBindings = new KeyBinding[]{InventoryWalk.mc.gameSettings.keyBindForward, InventoryWalk.mc.gameSettings.keyBindRight, InventoryWalk.mc.gameSettings.keyBindLeft, InventoryWalk.mc.gameSettings.keyBindBack, InventoryWalk.mc.gameSettings.keyBindJump};
        this.registerEventHandler(EventUpdate.class, event -> {
            if (!(InventoryWalk.mc.currentScreen instanceof GuiChat) && !(InventoryWalk.mc.currentScreen instanceof GuiEditSign)) {
                for (KeyBinding keyBinding : this.keyBindings) {
                    keyBinding.setPressed(Keyboard.isKeyDown(keyBinding.getKeyCode()));
                }
            }
        });
    }
}

