/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.misc;

import cafe.corrosion.event.impl.EventPacketIn;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.util.math.RandomUtil;
import cafe.corrosion.util.packet.PacketUtil;
import com.viaversion.viaversion.util.ChatColorUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleAttributes(name="KillInsults", description="Sends a message when you kill a player", category=Module.Category.MISC)
public class KillInsults
extends Module {
    private final List<String> killInsults;

    public KillInsults() {
        this.registerEventHandler(EventPacketIn.class, this::onPacket);
        this.killInsults = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/killsults.txt")))).lines().collect(Collectors.toList());
    }

    public void onPacket(EventPacketIn event) {
        if (event.getPacket() instanceof S02PacketChat) {
            String[] search;
            S02PacketChat packetChat = (S02PacketChat)event.getPacket();
            for (String string : search = new String[]{"killed by " + KillInsults.mc.thePlayer.getName(), "slain by " + KillInsults.mc.thePlayer.getName(), "void while escaping " + KillInsults.mc.thePlayer.getName(), "was killed with magic while fighting " + KillInsults.mc.thePlayer.getName(), "couldn't fly while escaping " + KillInsults.mc.thePlayer.getName(), "fell to their death while escaping " + KillInsults.mc.thePlayer.getName(), "was thrown into the void by " + KillInsults.mc.thePlayer.getName(), "foi morto por " + KillInsults.mc.thePlayer.getName(), "was thrown off a cliff by " + KillInsults.mc.thePlayer.getName(), KillInsults.mc.thePlayer.getName() + " killed ", "You killed ", "You received a reward for killing "}) {
                if (!ChatColorUtil.stripColor(packetChat.getChatComponent().getUnformattedText()).toLowerCase().contains(string.toLowerCase())) continue;
                String message = RandomUtil.choice(this.killInsults);
                PacketUtil.sendNoEvent(new C01PacketChatMessage(message));
                return;
            }
        }
    }
}

