package me.spec.eris.client.modules.movement;

import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;

public class Sprint extends Module {

    public Sprint(String racism) {
        super("Sprint", ModuleCategory.MOVEMENT, racism);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            mc.thePlayer.setSprinting(mc.thePlayer.isMoving() && !mc.thePlayer.isSneaking());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
        super.onDisable();
    }
}
