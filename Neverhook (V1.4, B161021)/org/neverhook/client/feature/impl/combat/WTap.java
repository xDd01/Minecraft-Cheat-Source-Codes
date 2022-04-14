package org.neverhook.client.feature.impl.combat;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.packet.EventSendPacket;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class WTap extends Feature {

    public WTap() {
        super("WTap", "Вы откидываете противника дальше", Type.Ghost);
    }

    @EventTarget
    public void onSendPacket(EventSendPacket event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                mc.player.setSprinting(false);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                mc.player.setSprinting(true);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
        }
    }

}
