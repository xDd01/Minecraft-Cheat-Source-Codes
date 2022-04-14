package org.neverhook.client.feature.impl.misc;

import net.minecraft.client.gui.GuiGameOver;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.helpers.misc.ChatHelper;
import org.neverhook.client.ui.notification.NotificationManager;
import org.neverhook.client.ui.notification.NotificationType;

public class DeathCoordinates extends Feature {

    public DeathCoordinates() {
        super("DeathCoordinates", "Показывает координаты смерти игрока", Type.Misc);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.player.getHealth() < 1 && mc.currentScreen instanceof GuiGameOver) {
            int x = mc.player.getPosition().getX();
            int y = mc.player.getPosition().getY();
            int z = mc.player.getPosition().getZ();
            if (mc.player.ticksExisted % 20 == 0) {
                NotificationManager.publicity("Death Coordinates", "X: " + x + " Y: " + y + " Z: " + z, 10, NotificationType.INFO);
                ChatHelper.addChatMessage("Death Coordinates: " + "X: " + x + " Y: " + y + " Z: " + z);
            }
        }
    }
}