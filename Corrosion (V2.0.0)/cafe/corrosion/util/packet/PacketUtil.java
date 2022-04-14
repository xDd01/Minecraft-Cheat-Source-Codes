/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void send(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendNoEvent(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void sendPacketDelayed(Packet<?> packet, long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                PacketUtil.sendNoEvent(packet);
            }
            catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }).start();
    }

    public static void sendTimes(int amount, Packet<?> packet) {
        for (int i2 = 0; i2 < amount; ++i2) {
            PacketUtil.sendNoEvent(packet);
        }
    }
}

