package org.neverhook.client.feature.impl.misc;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventPreMotion;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class SelfDamage extends Feature {

    private int jumps = 0;

    public SelfDamage() {
        super("SelfDamage", "Вы наносите себе дамаг", Type.Misc);
    }

    public void onEnable() {
        this.jumps = 0;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventPreMotion event) {
        if (jumps < 14) {
            mc.timer.renderPartialTicks = 4;
            for (int i = 0; i < 20; i++) {
                event.setOnGround(false);
            }
        }
        if (mc.player.onGround) {
            if (jumps < 14) {
                mc.player.jump();
                jumps++;
            } else {
                state();
            }
        }
    }
}
