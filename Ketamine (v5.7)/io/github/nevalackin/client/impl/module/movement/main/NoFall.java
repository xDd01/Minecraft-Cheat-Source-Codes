package io.github.nevalackin.client.impl.module.movement.main;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.event.packet.SendPacketEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.util.movement.JumpUtil;
import io.github.nevalackin.client.util.movement.MovementUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;
import java.util.List;

public final class NoFall extends Module {

    private final EnumProperty<Mode> modeProperty = new EnumProperty<>("Mode", Mode.EDIT);
    private final BooleanProperty roundPosProperty = new BooleanProperty("Round Pos", true,
            () -> this.modeProperty.getValue() != Mode.NO_GROUND);

    private AntiVoid antivoid;

    public NoFall() { // Antivoid Courtesy of Slosa
        super("No Fall", Category.MOVEMENT, Category.SubCategory.MOVEMENT_MAIN);

        this.setSuffix(() -> this.modeProperty.getValue().toString());
        this.register(this.modeProperty, this.roundPosProperty);
    }

    @EventLink(4)
    private final Listener<UpdatePositionEvent> onUpdatePosition = event -> {
        if (event.isPre()) {
            if (antivoid.falling) return;
            if (this.modeProperty.getValue().isRequiresRounding()) {
                if (this.canRoundPos(event.getPosY()) && this.modeProperty.getValue().onUpdatePosition(this.mc.thePlayer, event))
                    this.roundPosition(event);
            } else {
                this.modeProperty.getValue().onUpdatePosition(this.mc.thePlayer, event);
            }
        }
    };

    private boolean canRoundPos(final double currentPos) {
        if (this.roundPosProperty.check() && this.roundPosProperty.getValue()) {
            final double roundedPos = Math.round(currentPos / 0.015625F) * 0.015625F;
            return Math.abs(currentPos - roundedPos) <= 0.005;
        }

        return true;
    }

    private void roundPosition(final UpdatePositionEvent event) {
        if (this.roundPosProperty.getValue()) {
            event.setPosY(Math.round(event.getPosY() / 0.015625F) * 0.015625F);
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @FunctionalInterface
    private interface OnUpdateFunc {
        boolean onUpdate(final EntityPlayerSP player,
                         final UpdatePositionEvent event);
    }

    private enum Mode {
        PACKET("Packet", ((player, event) -> {
            if (player.fallDistance > JumpUtil.getMinFallDist(player)) {
                player.sendQueue.sendPacket(new C03PacketPlayer(true));
                player.fallDistance = 0.0F;
                return true;
            }
            return false;
        }), true),
        NO_GROUND("No Ground", ((player, event) -> {
            event.setOnGround(false);
            return false;
        }), false),
        NONE("None", null, false),
        EDIT("Edit", ((player, event) -> {
            if (player.fallDistance > JumpUtil.getMinFallDist(player)) {
                event.setOnGround(true);
                player.fallDistance = 0.0F;
                return true;
            }
            return false;
        }), true);

        private final String name;
        private final OnUpdateFunc onUpdate;
        private final boolean requiresRounding;

        Mode(String name, OnUpdateFunc onUpdate, boolean requiresRounding) {
            this.name = name;
            this.onUpdate = onUpdate;
            this.requiresRounding = requiresRounding;
        }

        public boolean onUpdatePosition(final EntityPlayerSP player, final UpdatePositionEvent event) {
            if (this.onUpdate == null) return false;
            return this.onUpdate.onUpdate(player, event);
        }

        public boolean isRequiresRounding() {
            return requiresRounding;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
