package org.neverhook.client.feature.impl.misc;

import net.minecraft.network.play.client.CPacketConfirmTeleport;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.packet.EventSendPacket;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.settings.impl.BooleanSetting;

public class PortalFeatures extends Feature {

    public static BooleanSetting chat = new BooleanSetting("Chat", true, () -> true);
    public static BooleanSetting cancelTeleport = new BooleanSetting("Cancel Teleport", true, () -> true);

    public PortalFeatures() {
        super("PortalFeatures", "Позволяет открыть чат в портале", Type.Misc);
        addSettings(chat, cancelTeleport);
    }

    @EventTarget
    public void onSendPacket(EventSendPacket event) {
        if (event.getPacket() instanceof CPacketConfirmTeleport && cancelTeleport.getBoolValue()) {
            event.setCancelled(true);
        }
    }
}
