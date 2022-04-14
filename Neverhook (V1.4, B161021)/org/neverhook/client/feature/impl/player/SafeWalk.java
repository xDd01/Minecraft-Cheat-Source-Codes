package org.neverhook.client.feature.impl.player;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.motion.EventSafeWalk;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class SafeWalk extends Feature {

    public SafeWalk() {
        super("SafeWalk", "Не дает упасть вам с блока", Type.Player);
    }

    @EventTarget
    public void onSafeWalk(EventSafeWalk event) {
        if (mc.player == null || mc.world == null)
            return;
        event.setCancelled(mc.player.onGround);
    }
}
