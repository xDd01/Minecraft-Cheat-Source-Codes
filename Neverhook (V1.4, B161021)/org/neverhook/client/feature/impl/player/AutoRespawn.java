package org.neverhook.client.feature.impl.player;

import net.minecraft.client.gui.GuiGameOver;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class AutoRespawn extends Feature {

    public AutoRespawn() {
        super("AutoRespawn", "Автоматический респавн при смерти", Type.Player);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.player.getHealth() < 0 || !mc.player.isEntityAlive() || mc.currentScreen instanceof GuiGameOver) {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }
}
