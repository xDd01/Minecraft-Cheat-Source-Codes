/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.feature.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.impl.EventPacketOut;
import cafe.corrosion.social.feature.Feature;
import cafe.corrosion.util.player.PlayerUtil;
import java.util.Arrays;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class CommandFeature
extends Feature {
    public CommandFeature() {
        this.registerEventHandler(EventPacketOut.class, event -> {
            if (!(event.getPacket() instanceof C01PacketChatMessage)) {
                return;
            }
            String message = ((C01PacketChatMessage)event.getPacket()).getMessage();
            if (!message.startsWith("-")) {
                return;
            }
            String[] context = message.substring(1).split(" ");
            if (context.length < 1) {
                PlayerUtil.sendMessage("Invalid command!");
                return;
            }
            String commandName = context[0];
            Corrosion.INSTANCE.getCommandManager().processCommand(commandName, Arrays.copyOfRange(context, 1, context.length));
            event.setCancelled(true);
        });
    }
}

