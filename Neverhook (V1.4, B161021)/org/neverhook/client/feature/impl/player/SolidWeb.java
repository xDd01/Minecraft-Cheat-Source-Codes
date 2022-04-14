package org.neverhook.client.feature.impl.player;

import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventWebSolid;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class SolidWeb extends Feature {

    public SolidWeb() {
        super("SolidWeb", "Делает паутину полноценным блоком", Type.Misc);
    }

    @EventTarget
    public void onWebSolid(EventWebSolid event) {
        event.setCancelled(true);
    }
}
