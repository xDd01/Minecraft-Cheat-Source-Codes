package org.neverhook.client.feature.impl.player;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventPreMotion;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.settings.impl.ListSetting;

public class FastEat extends Feature {

    private final ListSetting modeFastEat;

    public FastEat() {
        super("FastEat", "Позволяет быстро использовать еду", Type.Player);
        this.modeFastEat = new ListSetting("FastEat Mode", "Matrix", () -> true, "Matrix", "Vanilla");
        addSettings(modeFastEat);
    }

    @EventTarget
    public void onUpdate(EventPreMotion event) {
        String mode = modeFastEat.getOptions();
        this.setSuffix(mode);
        if (mode.equalsIgnoreCase("Matrix")) {
            if (mc.player.getItemInUseMaxCount() >= 12 && (mc.player.isEating() || mc.player.isDrinking())) {
                for (int i = 0; i < 10; i++) {
                    mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
                }
                mc.player.stopActiveHand();
            }
        } else if (mode.equalsIgnoreCase("Vanilla")) {
            if (mc.player.getItemInUseMaxCount() == 16 && (mc.player.isEating() || mc.player.isDrinking())) {
                for (int i = 0; i < 21; ++i) {
                    mc.player.connection.sendPacket(new CPacketPlayer(true));
                }
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        super.onDisable();
    }
}
