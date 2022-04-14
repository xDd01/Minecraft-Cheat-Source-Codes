package io.github.nevalackin.client.impl.module.misc.player;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.event.packet.SendPacketEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.event.world.LoadWorldEvent;
import io.github.nevalackin.client.util.math.MathUtil;
import io.github.nevalackin.client.util.misc.TimeUtil;
import io.github.nevalackin.client.util.movement.MovementUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayDeque;
import java.util.Deque;

public final class PingSpoof extends Module {

    private long packetsToCancel;
    private final TimeUtil S08Delay = new TimeUtil();
    private final TimeUtil packetDelay = new TimeUtil();
    private final Deque<TimestampedPacket> packetQueue = new ArrayDeque<>();

    public PingSpoof() {
        super("Ping Spoof", Category.MISC, Category.SubCategory.MISC_PLAYER);
    }

    @EventLink
    private final Listener<LoadWorldEvent> onLoadWorld = event -> {
        packetQueue.clear();
    };

    @EventLink
    private final Listener<UpdatePositionEvent> onUpdatePosition = event -> {
        if (!event.isPre()) return;

        if (packetsToCancel > 0) return;

        if (packetDelay.passed(mc.thePlayer.ticksExisted < 120 ? 1920 : MathUtil.getRandomInRange(1L, 250L))) {
            while (!packetQueue.isEmpty())
                mc.thePlayer.sendQueue.sendPacketDirect(packetQueue.removeLast().packet);
            packetDelay.reset();
        }
        if (mc.thePlayer.ticksExisted == 120) {
            KetamineClient.getInstance().getNotificationManager().add(NotificationType.SUCCESS, getName(), "Successfully Disabled Watchdog Checks", 2500L);
            event.setPosX(event.getPosX() + 1);
            event.setPosZ(event.getPosZ() + 1);
        }
    };

    @EventLink
    private final Listener<SendPacketEvent> onSendPacket = event -> {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer) {
            final C03PacketPlayer C03 = (C03PacketPlayer) packet;
            if (packetsToCancel > 0) {
                event.setCancelled();
                packetsToCancel--;
                return;
            }
            if (!C03.isMoving() && C03.getRotating()) {
                event.setCancelled();
            }
        }

        if (mc.thePlayer.ticksExisted < 120) {
            if (packet instanceof C03PacketPlayer) {
                final C03PacketPlayer C03 = (C03PacketPlayer) packet;
                if (!C03.isMoving() && !C03.getRotating()) {
                    event.setCancelled();
                } else {
                    packetQueue.push(new TimestampedPacket(packet));
                    event.setCancelled();
                }
            }
            if (packet instanceof C00PacketKeepAlive) {
                packetQueue.push(new TimestampedPacket(packet));
                event.setCancelled();
            }
            if (packet instanceof C0FPacketConfirmTransaction) {
                packetQueue.push(new TimestampedPacket(packet));
                event.setCancelled();
            }
        }
    };

    @EventLink
    @SuppressWarnings({"rawtypes, unChecked"})
    private final Listener<ReceivePacketEvent> onReceivePacket = event -> {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook S08 = (S08PacketPlayerPosLook) packet;

            if (mc.thePlayer.ticksExisted < 5 || !MovementUtil.isBlockUnder(mc) || MovementUtil.isOverVoid(mc))
                return;

            if (S08Delay.passed(2000)) {
                if (mc.thePlayer.getDistance(S08.getX(), S08.getY(), S08.getZ()) < 10) {
                    packetsToCancel = 6;
                    event.setCancelled();
                }
                S08Delay.reset();
            }
        }
    };

    @Override
    public void onEnable() {
        packetQueue.clear();
    }

    @Override
    public void onDisable() {

    }


    private static class TimestampedPacket {
        private final Packet<?> packet;
        private long timestamp;

        public TimestampedPacket(final Packet<?> packet) {
            this.packet = packet;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
