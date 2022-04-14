package org.neverhook.client.feature.impl.player;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.packet.EventReceivePacket;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class NoRotate extends Feature {

    public NoRotate() {
        super("NoServerRotation", "Убирает ротацию со стороны сервера", Type.Player);
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    }
}
