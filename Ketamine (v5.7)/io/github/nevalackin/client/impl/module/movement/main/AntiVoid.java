package io.github.nevalackin.client.impl.module.movement.main;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.event.packet.SendPacketEvent;
import io.github.nevalackin.client.impl.event.player.MoveEvent;
import io.github.nevalackin.client.impl.module.movement.extras.Flight;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.client.util.movement.MovementUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;
import java.util.List;

public class AntiVoid extends Module {

    private double x, y, z;
    public static boolean falling;
    private List<Packet> packets = new ArrayList<>();
    private final DoubleProperty fallDistProperty = new DoubleProperty("Fall Distance", 7.0, 3, 12.0, 1);

    public AntiVoid() { //Courtesy of Slosa
        super("AntiVoid", Category.MOVEMENT, Category.SubCategory.MOVEMENT_MAIN);
        register(fallDistProperty);
    }

    @EventLink
    private final Listener<MoveEvent> onMove = event -> {
        if (MovementUtil.isBlockUnderSlosa() && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && !mc.gameSettings.keyBindSneak.isPressed()) {
            x = mc.thePlayer.posX;
            y = mc.thePlayer.posY;
            z = mc.thePlayer.posZ;
        }
        if (falling && mc.thePlayer.fallDistance > fallDistProperty.getValue() && !mc.gameSettings.keyBindSneak.isPressed()) {
            packets.clear();
            mc.thePlayer.setPositionAndUpdate(x, y, z);
        }
    };

    @EventLink
    private final Listener<SendPacketEvent> onSend = event -> {
        if (event.getPacket() instanceof C03PacketPlayer && !mc.gameSettings.keyBindSneak.isPressed()) {
            if (!MovementUtil.isBlockUnderSlosa() && !mc.gameSettings.keyBindSneak.isPressed()) {
                packets.add(event.getPacket());
                event.setCancelled(true);
                falling = true;
            } else {
                if (mc.thePlayer.fallDistance < fallDistProperty.getValue() && !mc.gameSettings.keyBindSneak.isPressed()) {
                    if (falling) {
                        for (Packet packet : packets) {
                            mc.thePlayer.sendQueue.sendPacketDirect(packet);
                        }
                        packets.clear();
                        falling = false;
                    }
                }
            }
        }
    };

    @EventLink
    private final Listener<ReceivePacketEvent> onReceive = event -> {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && !mc.gameSettings.keyBindSneak.isPressed()) {
            S08PacketPlayerPosLook posLook = (S08PacketPlayerPosLook) event.getPacket();
            for (Packet packet : packets) {
                C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
                if (packetPlayer.getPositionX() == posLook.getX() && packetPlayer.getPositionY() == posLook.getY() && packetPlayer.getPositionZ() == posLook.getZ() && !mc.gameSettings.keyBindSneak.isPressed()) {
                    packets.clear();
                    falling = false;
                }
            }
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
