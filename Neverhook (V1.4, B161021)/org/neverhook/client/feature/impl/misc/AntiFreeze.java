package org.neverhook.client.feature.impl.misc;

import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.packet.EventReceivePacket;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class AntiFreeze extends Feature {

    public AntiFreeze() {
        super("Anti Freeze", "Убирает заморозку игрока", Type.Misc);
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketEntityTeleport) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof SPacketOpenWindow) {
            event.setCancelled(true);
        }
    }
}
