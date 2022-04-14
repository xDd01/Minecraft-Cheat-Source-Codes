package org.neverhook.client.feature.impl.movement;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventPreMotion;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class AirJump extends Feature {

    public AirJump() {
        super("AirJump", "Позволяет прыгать по воздуху", Type.Movement);
    }

    @EventTarget
    public void onPreUpdate(EventPreMotion event) {
        if (mc.gameSettings.keyBindJump.pressed) {
            mc.player.onGround = true;
        }
    }
}
