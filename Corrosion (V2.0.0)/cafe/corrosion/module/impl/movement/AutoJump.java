/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;

@ModuleAttributes(name="Auto Jump", description="Automatically jumps when you're on the ground", category=Module.Category.MOVEMENT)
public class AutoJump
extends Module {
    public AutoJump() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (event.isOnGround()) {
                AutoJump.mc.gameSettings.keyBindJump.setPressed(true);
            }
        });
    }

    @Override
    public void onDisable() {
        AutoJump.mc.gameSettings.keyBindJump.setPressed(false);
    }
}

